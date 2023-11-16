/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lemminx.extensions.contentmodel.participants.diagnostics;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.extensions.relaxng.xml.validator.ExternalRelaxNGValidator;
import org.eclipse.lemminx.extensions.xerces.AbstractLSPErrorReporter;
import org.eclipse.lemminx.extensions.xerces.ExternalXMLDTDValidator;
import org.eclipse.lemminx.extensions.xerces.LSPSecurityManager;
import org.eclipse.lemminx.extensions.xerces.LSPXMLEntityManager;
import org.eclipse.lemminx.extensions.xerces.xmlmodel.XMLModelAwareParserConfiguration;

/**
 * Custom Xerces XML parser configuration to :
 * 
 * <ul>
 * <li>disable only DTD validation if required</li>
 * <li>disable doctype declaration according validation settings</li>
 * <li>disable external entities according validation settings</li>
 * <li>manage a custom grammar pool to retrieve compiled XML Schema/DTD from a
 * given XML file path</li>
 * </ul>
 *
 */
public class LSPXMLParserConfiguration extends XMLModelAwareParserConfiguration {

	/** property identifier: security manager. */
	private static final String SECURITY_MANAGER = Constants.XERCES_PROPERTY_PREFIX
			+ Constants.SECURITY_MANAGER_PROPERTY;

	private final boolean disableDTDValidation;
	private ExternalXMLDTDValidator externalDTDValidator;

	private ExternalRelaxNGValidator externalRelaxNGValidator;

	public LSPXMLParserConfiguration(XMLGrammarPool grammarPool, boolean disableDTDValidation,
			LSPErrorReporterForXML reporterForXML, LSPErrorReporterForXML reporterForGrammar,
			LSPXMLEntityManager entityManager, XMLValidationSettings validationSettings) {
		super(null, grammarPool, reporterForGrammar);
		this.disableDTDValidation = disableDTDValidation;
		// Disable DOCTYPE declaration if settings is set to true.
		boolean disallowDocTypeDecl = validationSettings != null ? validationSettings.isDisallowDocTypeDecl() : false;
		super.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disallowDocTypeDecl);
		// Resolve external entities if settings is set to true.
		boolean resolveExternalEntities = validationSettings != null ? validationSettings.isResolveExternalEntities()
				: false;
		super.setFeature("http://xml.org/sax/features/external-general-entities", resolveExternalEntities);
		super.setFeature("http://xml.org/sax/features/external-parameter-entities", resolveExternalEntities);
		// Enable xi:include validation if settings is set to true.
		boolean isXIncludeEnabled = validationSettings != null ? validationSettings.getXInclude().isEnabled() : false;
		super.setFeature(XINCLUDE_FEATURE, isXIncludeEnabled);
		// Security manager
		SecurityManager securityManager = LSPSecurityManager.getSecurityManager();
		super.setProperty(SECURITY_MANAGER, securityManager);
		fErrorReporter = reporterForXML;

		fEntityManager = entityManager;
		fProperties.put(ENTITY_MANAGER, fEntityManager);
		addCommonComponent(fEntityManager);
	}

	@Override
	protected void reset() throws XNIException {
		super.reset();
		if (disableDTDValidation) {
			// reset again DTD validator by setting "http://xml.org/sax/features/validation"
			// to false.
			disableDTDValidation();
		}
	}

	private void disableDTDValidation() {
		XMLDTDValidator validator = (XMLDTDValidator) super.getProperty(DTD_VALIDATOR);
		if (validator != null) {
			// Calling XMLDTDValidator#setFeature("http://xml.org/sax/features/validation",
			// false) does nothing.
			// The only way to set "http://xml.org/sax/features/validation" to false is to
			// call XMLDTDValidator#reset with the proper component.
			// We need to create a new component and not use the current configuration
			// otherwise set
			// "http://xml.org/sax/features/validation" to the configuration
			// will update the other component and will disable validation too for XML
			// Schema
			XMLComponentManager disableDTDComponent = new XMLComponentManager() {

				@Override
				public Object getProperty(String propertyId) throws XMLConfigurationException {
					return LSPXMLParserConfiguration.this.getProperty(propertyId);
				}

				@Override
				public boolean getFeature(String featureId) throws XMLConfigurationException {
					if ("http://xml.org/sax/features/validation".equals(featureId)) {
						return false;
					}
					return LSPXMLParserConfiguration.this.getFeature(featureId);
				}
			};
			validator.reset(disableDTDComponent);
		}
	}

	@Override
	protected void configurePipeline() {
		super.configurePipeline();
		configureExternalDTDPipeline();
		configureExternalRelaxNGPipeline();
	}

	@Override
	protected void configureXML11Pipeline() {
		super.configureXML11Pipeline();
		configureExternalDTDPipeline();
		configureExternalRelaxNGPipeline();
	}

	private void configureExternalDTDPipeline() {
		if (externalDTDValidator == null) {
			externalDTDValidator = new ExternalXMLDTDValidator();
			addCommonComponent(externalDTDValidator);
			externalDTDValidator.reset(this);
		}
		// configure XML document pipeline: insert after DTDValidator and
		// before XML Schema validator
		XMLDocumentSource prev = null;
		if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
			// we don't have to worry about fSchemaValidator being null, since
			// super.configurePipeline() instantiated it if the feature was set
			prev = fSchemaValidator.getDocumentSource();
		}
		// Otherwise, insert after the last component in the pipeline
		else {
			prev = fLastComponent;
			fLastComponent = externalDTDValidator;
		}

		XMLDocumentHandler next = prev.getDocumentHandler();
		prev.setDocumentHandler(externalDTDValidator);
		externalDTDValidator.setDocumentSource(prev);
		if (next != null) {
			externalDTDValidator.setDocumentHandler(next);
			next.setDocumentSource(externalDTDValidator);
		}
		if (fSchemaValidator != null) {
			// Set the LSP reporter for Xerces SchemaDOMParser to collect XML Schema error
			// in the case of schema have some error (ex : syntax error)
			LSPXMLEntityManager entityManager = new LSPXMLEntityManager((AbstractLSPErrorReporter) fErrorReporter,
					null);
			AbstractLSPErrorReporter.initializeReporter(fSchemaValidator, getReporterForGrammar(), entityManager);
		}
	}

	private void configureExternalRelaxNGPipeline() {
		if (externalRelaxNGValidator == null) {
			externalRelaxNGValidator = new ExternalRelaxNGValidator();
			addCommonComponent(externalRelaxNGValidator);
			externalRelaxNGValidator.reset(this);
		}
		// configure XML document pipeline: insert after DTDValidator and
		// before XML Schema validator
		XMLDocumentSource prev = null;
		if (fFeatures.get(XMLSCHEMA_VALIDATION) == Boolean.TRUE) {
			// we don't have to worry about fSchemaValidator being null, since
			// super.configurePipeline() instantiated it if the feature was set
			prev = fSchemaValidator.getDocumentSource();
		}
		// Otherwise, insert after the last component in the pipeline
		else {
			prev = fLastComponent;
			fLastComponent = externalRelaxNGValidator;
		}

		XMLDocumentHandler next = prev.getDocumentHandler();
		prev.setDocumentHandler(externalRelaxNGValidator);
		externalRelaxNGValidator.setDocumentSource(prev);
		if (next != null) {
			externalRelaxNGValidator.setDocumentHandler(next);
			next.setDocumentSource(externalRelaxNGValidator);
		}
	}

	@Override
	protected void checkProperty(String propertyId) throws XMLConfigurationException {
		if (ExternalRelaxNGValidator.RELAXNG.equals(propertyId)) {
			return;
		}
		super.checkProperty(propertyId);
	}

}