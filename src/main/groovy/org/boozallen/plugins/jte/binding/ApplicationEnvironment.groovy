/*
   Copyright 2018 Booz Allen Hamilton

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package org.boozallen.plugins.jte.binding

import org.boozallen.plugins.jte.binding.formElements.*
import org.boozallen.plugins.jte.config.*
import org.jenkinsci.plugins.workflow.cps.CpsScript
import hudson.Extension 

/*
    represents an immutable application environment. 
*/
@Extension class ApplicationEnvironment extends TemplatePrimitive{
    String var_name
    String short_name
    String long_name
    final def config

    final static String CONFIG_FIELD = "application_environments"
    
    ApplicationEnvironment(){}

    ApplicationEnvironment(String var_name, Map _config){ 
        this.var_name = var_name

        short_name = _config.short_name ?: var_name 
        long_name = _config.long_name ?: var_name 
        
        config = _config - _config.subMap(["short_name", "long_name"])
        /*
            TODO: 
                this makes it so that changing <inst>.config.whatever = <some value> 
                will throw an UnsupportOperationException.  Need to figure out how to 
                throw TemplateConfigException instead for the sake of logging.
        */
        config = config.asImmutable()
    }

    String getName(){
        return CONFIG_FIELD
    }
    
    Object getProperty(String name){
        def meta = ApplicationEnvironment.metaClass.getMetaProperty(name)
        if (meta) {
            meta.getProperty(this)
        } else {
            if (config.containsKey(name)) return config.get(name)
            else return null
        }
    }

    void setProperty(String name, Object value){
        throw new TemplateConfigException("Can't modify Application Environment '${long_name}'. Application Environments are immutable.")
    }

    void throwPreLockException(){
        throw new TemplateException ("Application Environment ${long_name} already defined.")
    }

    void throwPostLockException(){
        throw new TemplateException ("Variable ${var_name} is reserved as an Application Environment")
    }

    /*
        inject ApplicationEnvironment objects into the binding. 

        example configuration: 

        application_environments{
            dev{
                openshift_url = "https://example.com:8443" 
            }
            test
            prod{
                long_name = "Production" 
            }
        }

        would create ApplicationEnvironment objects dev, test, and prod 
        that could be referenced from a pipeline template
    */
    @Extension static class Injector extends TemplatePrimitiveInjector {
        static void doInject(TemplateConfigObject config, CpsScript script){
            config.getConfig()."$ApplicationEnvironment.CONFIG_FIELD".each{ name, appEnvConfig ->
                ApplicationEnvironment appEnv = new ApplicationEnvironment(name, appEnvConfig)
                script.getBinding().setVariable(name, appEnv)
            }
        }
    }

    /*
    * Creates a list of form elements gives information about fields users need to fill out to configure a pipeline 
    */
    @Extension static class FormConfig extends JTEFormConfiguration{        
        static List<JTEFormElement> getFormElements() {
            List<JTEFormElement> formElements = new ArrayList() 
            formElements.push(new Input("Short Name", "short_name", "The short name is the variable that will be used within the Jenkinsfile to reference the long name.", "string") )
            formElements.push(new Input("Long Name", "long_name", "The long name is the actual name that will be used whenever the short name is referenced.", "string" ))
            return formElements; 
        }

        public String getDisplayName(){
            return "Application Environments"
        }

        public String getName(){
            return ApplicationEnvironment.CONFIG_FIELD
        }

        public String getDescription(){
            return "Enter the names for different Application Environments that you'd like to deploy to."
        }

        public boolean isUserAddable(){
            return true;
        }
    }

}