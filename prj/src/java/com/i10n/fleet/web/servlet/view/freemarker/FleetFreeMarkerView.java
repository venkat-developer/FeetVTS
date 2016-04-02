package com.i10n.fleet.web.servlet.view.freemarker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.StandardCompress;

/**
 * Custom {@link FreeMarkerView} for Fleetcheck project which enables
 * customization required in Freemarker View.
 * 
 * @author sabarish
 * 
 */
public class FleetFreeMarkerView extends FreeMarkerView {
    private static final String TRUE_TEMPLATE_MODEL = "true";
    private static final String FALSE_TEMPLATE_MODEL = "false";
    private static final String COMPRESSION_VARIABLE = "compression";

    @SuppressWarnings("rawtypes")
    @Override
    protected void processTemplate(Template template, Map model,
            HttpServletResponse response) throws IOException, TemplateException {
        setContentType(template, response);
        TemplateModel val = getConfiguration().getSharedVariable(COMPRESSION_VARIABLE);
        /**
         * To check if compression is enabled
         */
        if (null != val && TRUE_TEMPLATE_MODEL.equals(val.toString())) {
            StandardCompress compressor = StandardCompress.INSTANCE;
            Map<String, TemplateModel> args = new HashMap<String, TemplateModel>();
            for (Object varObj : getConfiguration().getSharedVariableNames().toArray()) {
                String varName = (String) varObj;
                TemplateModel value = getConfiguration().getSharedVariable(varName);
                /**
                 * This is done to convert all SimpleScalar whose value is
                 * either true or false to TemplateBooleanModel, this should
                 * have been done by spring but now the conversion happens here,
                 * and canbe removed when spring supports this
                 */
                if (!(value instanceof TemplateBooleanModel)) {
                    if (TRUE_TEMPLATE_MODEL.equals(value.toString())) {
                        value = TemplateBooleanModel.TRUE;
                    }
                    else if (FALSE_TEMPLATE_MODEL.equals(value.toString())) {
                        value = TemplateBooleanModel.FALSE;
                    }
                }
                args.put(varName, value);
            }

            final PrintWriter responseWriter = response.getWriter();
            final Writer compressorWriter = compressor.getWriter(responseWriter, args);
            template.process(model, compressorWriter);
            compressorWriter.close();
        }
        else {
            super.processTemplate(template, model, response);
        }

    }

    /**
     * The {@link FreeMarkerView} provided with the Spring distribution ignores
     * the content-type customization offered by the
     * {@link freemarker.ext.servlet.FreemarkerServlet}. Add it here as spring
     * hides it
     * 
     * @param template
     * @param response
     * 
     */
    protected void setContentType(Template template, HttpServletResponse response) {
        Object attrContentType = template.getCustomAttribute("content_type");

        if (null == attrContentType) {
            response.setContentType(DEFAULT_FTL_CONTENT_TYPE);
        }
        else {
            response.setContentType(attrContentType.toString());
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void render(Map model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        super.render(model, request, response);
    }

    @Override
    protected Template getTemplate(String name, Locale locale) throws IOException {
        return super.getTemplate(name, locale);
    }

    @SuppressWarnings("unused")
    private static final String SINGLE_LINE_KEY = "single_line";
    private static final String DEFAULT_FTL_CONTENT_TYPE = "text/html;charset=UTF-8";
}
