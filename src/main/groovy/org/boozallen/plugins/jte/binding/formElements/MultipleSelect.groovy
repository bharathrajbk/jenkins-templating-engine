/*
  Copyright © 2018 Booz Allen Hamilton. All Rights Reserved.
  This software package is licensed under the Booz Allen Public License. The license can be found in the License file or at http://boozallen.github.io/licenses/bapl
*/

package org.boozallen.plugins.jte.binding.formElements;

/**
* Form element with two adjacent lists, one with available options and one with selected options.
* The two menus are separated by buttons to move options between the two lists.
*/
public class MultipleSelect extends JTEFormElement {

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
* Arraylist of strings that presents to the user the options that can be selected for the left menu
*/
  private Object left_options;

/*
* Arraylist of strings that presents to the user the options that can be selected for the right menu
*/
  private Object right_options;  

/*
* title that describes what the left menu will show
*/
  private String left_title;

/*
* title that describes what the right menu will show
*/
  private String right_title;

  MultipleSelect(String display_name, String name, String description, String left_title, Object left_options, String right_title, Object right_options){
    this.display_name = display_name;
    this.name = name;
    this.description = description;
    this.left_title = left_title;
    this.right_title = right_title;
    this.left_options = left_options;
    this.right_options = right_options;
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

  public Object getLeftOptions(){
      return this.left_options;
  }

  public Object getRightOptions(){
      return this.right_options;
  }

  public String getLeftTitle(){
    return this.left_title;
  }

  public String getRightTitle(){
    return this.right_title;
  }
    
}