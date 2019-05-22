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
package org.boozallen.plugins.jte.config

import org.kohsuke.groovy.sandbox.GroovyInterceptor
import org.kohsuke.groovy.sandbox.GroovyInterceptor.Invoker

/*
  our sandbox.  just block all the things except the creation of
  the TemplateConfigBuilder base class and methods associated with that.

  The sandbox is having trouble recognizing the receiver as type TemplateConfigBuilder
  so backed off to checking if it's a Script object.
*/
class TemplateConfigDslSandbox extends GroovyInterceptor {
  @Override
  Object onMethodCall(Invoker invoker, Object receiver, String method, Object... args) throws Throwable {
    if (!(receiver in TemplateConfigBuilder)){
      throw new SecurityException("""
        onMethodCall:
        invoker -> ${invoker}
        receiver -> ${receiver}
        method -> ${method}
        args -> ${args}
      """)
    }
    return invoker.call(receiver,method,args);
  }

  @Override
  Object onStaticCall(Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
    throw new SecurityException("""
      onStaticCall:
      invoker -> ${invoker}
      receiver -> ${receiver}
      method -> ${method}
      args -> ${args}
    """)
  }

  @Override
  public Object onNewInstance(Invoker invoker, Class receiver, Object... args) throws Throwable {
    if (!(receiver in TemplateConfigBuilder)){
      throw new SecurityException("""
        onNewInstance:
        invoker -> ${invoker}
        receiver -> ${receiver}
        args -> ${args}
      """)
    }
  }

  @Override
  public void onSuperConstructor(Invoker invoker, Class receiver, Object... args) throws Throwable {
    if (!(receiver in TemplateConfigBuilder)){
      throw new SecurityException("""
        onSuperConstructor:
        invoker -> ${invoker}
        receiver -> ${receiver}
        args -> ${args}
      """)
    }
  }

  @Override
  public Object onSuperCall(Invoker invoker, Class senderType, Object receiver, String method, Object... args) throws Throwable {
    throw new SecurityException("""
      onSuperCall:
      invoker -> ${invoker}
      senderType -> ${senderType}
      receiver -> ${receiver}
      method -> ${method}
      args -> ${args}
    """)
  }

  @Override
  public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable {
    if (!(receiver in TemplateConfigBuilder)){
      throw new SecurityException("""
        onGetProperty:
        invoker -> ${invoker}
        receiver -> ${receiver}
        property -> ${property}
      """)
    }
    return invoker.call(receiver,property);
  }

  @Override
  public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable {
    if (!(receiver in TemplateConfigBuilder)){
      throw new SecurityException("""
        onSetProperty:
        invoker -> ${invoker}
        receiver -> ${receiver}
        method -> ${method}
        args -> ${args}
      """)
    }
    return invoker.call(receiver,property,value);
  }

  @Override
  public Object onGetAttribute(Invoker invoker, Object receiver, String attribute) throws Throwable {
    throw new SecurityException("""
      onGetAttribute:
      invoker -> ${invoker}
      receiver -> ${receiver}
      attribute -> ${attribute}
    """)
  }

  @Override
  public Object onSetAttribute(Invoker invoker, Object receiver, String attribute, Object value) throws Throwable {
    throw new SecurityException("""
      onSetAttribute:
      invoker -> ${invoker}
      receiver -> ${receiver}
      attribute -> ${attribute}
      value -> ${value}
    """)
  }

  @Override
  public Object onGetArray(Invoker invoker, Object receiver, Object index) throws Throwable {
    throw new SecurityException("""
      onGetArray:
      invoker -> ${invoker}
      receiver -> ${receiver}
      index -> ${index}
    """)
  }

  @Override
  public Object onSetArray(Invoker invoker, Object receiver, Object index, Object value) throws Throwable {
    throw new SecurityException("""
      onSetArray:
      invoker -> ${invoker}
      receiver -> ${receiver}
      index -> ${index}
      value -> ${value}
    """)
  }
}
