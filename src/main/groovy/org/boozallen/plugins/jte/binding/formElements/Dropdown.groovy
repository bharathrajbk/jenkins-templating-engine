/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package org.boozallen.plugins.jte.binding.formElements;


public class Dropdown extends JTEFormElement {

/*
* title of the configuration that will be displayed in the UI
*/
  private String display_name;

/*
* short name of the primitive’s configuration that will used as the field name
* when the configuration value is sent to the SDP API to create a pipeline configuration
*/
  private String name;

/*
* describes the use of the primitive in terms of how it relates to the user’s configuration of SDP.
*/
  private String description;

/*
* string that is shown and used as the default value of the input box
*/
  private String default_selected;

/*
* Arraylist of strings that presents to the user the options that can be selected
*/
  private ArrayList<String> options;


  Dropdown(String display_name, String name, String description, ArrayList<String> options){
    this.display_name = display_name;
    this.name = name;
    this.description = description;
    this.default_selected = "";
    this.options = options;
  }


  Dropdown(String display_name, String name, String description, String default_selected, ArrayList<String> options){
    this.display_name = display_name;
    this.name = name;
    this.description = description;
    this.default_selected = default_selected;
    this.options = options;

  }

  @Override
  public String getDisplayName(){
    return this.display_name;
  }

  @Override
  public String getName(){
    return this.name;
  }

  @Override
  public String getDescription(){
    return this.description;
  }

  public String getDefaultSelected(){
    return this.default_selected;

  }

  public ArrayList<String> getOptions(){
      return this.options;
  }
    
}