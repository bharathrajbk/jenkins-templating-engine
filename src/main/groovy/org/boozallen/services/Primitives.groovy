package org.boozallen.services

import hudson.model.Api;
import hudson.ExtensionList;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import org.boozallen.plugins.jte.binding.JTEFormConfiguration;



import java.io.IOException;

//Sample URL for this enpdoint would be http://localhost:8080/plugin/jte/primitives/
public class Primitives extends Api{
    
    public Primitives(Object bean) {
        super(bean);
    }

    public JSONObject getInformation() {
        JSONObject json = new JSONObject();

        ExtensionList<JTEFormConfiguration> formConfigs = JTEFormConfiguration.all() 
        formConfigs.each{ form ->
            JSONObject primitiveJSON = new JSONObject();

            primitiveJSON.put("name", form.getName())
            primitiveJSON.put("display_name", form.getDisplayName())
            primitiveJSON.put("description", form.getDescription())
            primitiveJSON.put("user_addable", form.isUserAddable())
            
        ArrayList<JSONObject> configs = new ArrayList()

        def formElements = form.getFormElements();

        formElements.each{ formElement ->
            JSONObject configJSON = new JSONObject(); 
            configJSON.put("name", formElement.getName());
            configJSON.put("display_name", formElement.getDisplayName());
            configJSON.put("description", formElement.getDescription());
            String classTypes = formElement.getClass();
            def parsedTypes = classTypes.split("\\.")
            String fieldType = Primitives.getLastElement(parsedTypes)
            configJSON.put("field_type", fieldType)
            if (fieldType.equals("Input")){
                configJSON.put("default_value", formElement.getDefaultValue())
                configJSON.put("input_type", formElement.getInputType())
            }
            else if(fieldType.equals("Dropdown")){
                configJSON.put("default_selected", formElement.getDefaultSelected())
                configJSON.put("options", formElement.getOptions())
            }
            else if (fieldType.equals("MultipleSelect")){
                configJSON.put("left_title", formElement.getLeftTitle())
                configJSON.put("right_title", formElement.getRightTitle())
                configJSON.put("left_options", formElement.getLeftOptions())
                configJSON.put("right_options", formElement.getRightOptions())

            }
            else {

            }
            configs.push(configJSON);
        }

        primitiveJSON.put("configurations", configs);
            
        json.put(form.getName(), primitiveJSON);
            
        }

        

        return json;
    }

    public static String getLastElement(String[] list){
        return list[list.length-1]
    }

    public JSONObject createConfigObj(String fieldType){
            JSONObject configJSON = new JSONObject();


    }

    // public JSONObject getPrimitivesInfo(JTEFormConfiguration form){
    //     JSONObject primitiveJSON = new JSONObject();

    //     primitiveJSON.put("name", form.getName())
    //     primitiveJSON.put("display_name": form.getDisplayName())


    // }

    //Sample URL for this enpdoint would be http://localhost:8080/plugin/jte/primitives/all
    @WebMethod(name = "all")
    public void getPrimitives(StaplerRequest req, StaplerResponse rsp)
        throws IOException {
        rsp.setContentType("application/json");
        
        //Enables pretty printed response
        if (req.getQueryString() != null && !req.getQueryString().isEmpty() && req.getQueryString().equals("pretty=true")) {
            rsp.getWriter().write(getInformation().toString(4));
        }
        else {
            rsp.getWriter().write(getInformation().toString());
        }

            return;
        }

}

