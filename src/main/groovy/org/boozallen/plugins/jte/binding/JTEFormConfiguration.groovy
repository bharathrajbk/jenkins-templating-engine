
/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package org.boozallen.plugins.jte.binding

import org.boozallen.plugins.jte.binding.formElements.*
import hudson.ExtensionPoint
import hudson.ExtensionList 
import jenkins.model.Jenkins 

abstract class JTEFormConfiguration implements ExtensionPoint, JTEPrimitiveConfiguration{
    
    static List<JTEFormElement> getFormElements(){}

    // used to get all JTEFormConfigurations
    static ExtensionList<JTEFormConfiguration> all(){
        return Jenkins.getActiveInstance().getExtensionList(JTEFormConfiguration)
    }

}