package org.boozallen.services


import hudson.Extension;
import hudson.Plugin;
import org.kohsuke.stapler.export.ExportedBean;
import org.boozallen.services.Primitives

/**
* The API Endpoint for this would be /plugin/jte
*/ 

@Extension
@ExportedBean
public class JTE extends Plugin {

    public Primitives getPrimitives() {
        return new Primitives(this);
    }
}
