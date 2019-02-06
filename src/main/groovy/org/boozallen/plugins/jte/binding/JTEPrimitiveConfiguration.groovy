package org.boozallen.plugins.jte.binding


public interface JTEPrimitiveConfiguration {

/*
* string that will be displayed in the UI to show the primitive’s full name
*/
    public String getDisplayName();

/*
* used as the field name when the configuration value is sent
* to the SDP API to create a pipeline configuration.
*/
    public String getName();

/**
* describes what the configuration is used for when setting up SDP in relation to the user
*/
    public String getDescription();


/*
* specifies if a user can add multiple instances of the given inputs within the “configurations” property
* by adding a “Add primitive_name” button within the primitive’s configurations
*/
    public boolean isUserAddable();

}