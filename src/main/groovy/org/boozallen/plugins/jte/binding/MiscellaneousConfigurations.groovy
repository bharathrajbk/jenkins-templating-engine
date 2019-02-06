package org.boozallen.plugins.jte.binding

import org.boozallen.plugins.jte.binding.formElements.*
import org.boozallen.plugins.jte.binding.JTEFormConfiguration
import hudson.Extension 

/*
* Creates a list of form elements that gives information about fields users need to fill out to configure a pipeline 
*/
    @Extension class MiscellaneousConfigurations extends JTEFormConfiguration{        
        static List<JTEFormElement> getFormElements() {
            List<JTEFormElement> formElements = new ArrayList() 
            ArrayList<String> allowTenantFileOptions = new ArrayList();
            allowTenantFileOptions.push("True")
            allowTenantFileOptions.push("False");
            formElements.push(new Dropdown("Allow Tenant Jenkinsfile", "allow_tenant_jenkinsfile", "Determines if a tenant repository can have a Jenkinsfile that overrides its parent Jenkinsfile.", "True", allowTenantFileOptions ) )
            formElements.push(new Input("SDP Image Repository", "sdp_image_repository", "URL of the Docker Repository that SDP will use to pull its Docker images from.", "string" ))
            formElements.push(new Input("SDP Image Repository Credential", "sdp_image_repository_credential", "The name of the SDP credentials used to access the SDP Image Repository.", "string" ))
            formElements.push(new Input("Application Image Repository", "application_image_repository", "URL of the Docker Repository that SDP will use to pull and push the Docker images for stored web application Docker images.", "string" ))
            formElements.push(new Input("Application Image Repository Credential", "application_image_repository_credential", "The name of the credentials used to access the application image repository.", "string" ))
            return formElements; 
        }

        public String getDisplayName(){
            return "Miscellaneous Configurations"
        }

        public String getName(){
            return "miscellaneous_configs"
        }

        public String getDescription(){
            return "Enter general configurations for Your SDP Instance."
        }

        public boolean isUserAddable(){
            return false;
        }
    }