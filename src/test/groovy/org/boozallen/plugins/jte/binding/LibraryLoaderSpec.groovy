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

import spock.lang.*
import org.boozallen.plugins.jte.Utils
import org.boozallen.plugins.jte.config.GovernanceTier
import org.boozallen.plugins.jte.config.TemplateConfigException
import org.boozallen.plugins.jte.config.TemplateConfigObject
import org.boozallen.plugins.jte.config.TemplateLibrarySource
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.junit.ClassRule
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.WithoutJenkins

class LibraryLoaderSpec extends Specification {

    @Shared @ClassRule JenkinsRule jenkins = new JenkinsRule()
    CpsScript script = Mock()
    PrintStream logger = Mock()

    def setup(){
        GroovySpy(Utils, global:true)
        _ * Utils.getCurrentJob() >> jenkins.createProject(WorkflowJob)
        _ * Utils.getLogger() >> logger
    }

    @WithoutJenkins
    def "missing library throws exception"(){
        setup:
            TemplateLibrarySource libSource = Mock{
                hasLibrary("test_library") >> false
            }

            GovernanceTier tier = GroovyMock(global:true){
                getLibrarySources() >> [ libSource ]
            }
            GovernanceTier.getHierarchy() >> [ tier ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    test_library: [:]
                ]
            ])
        when:
            LibraryLoader.doInject(config, script)
        then:
            TemplateConfigException ex = thrown()
            ex.message == "Library test_library Not Found."
    }

    @WithoutJenkins
    def "when library source has library, loadLibrary is called"(){
        setup:
            TemplateLibrarySource s = Mock{
                hasLibrary("test_library") >> true
            }
            GovernanceTier tier = GroovyMock(global: true){
                getLibrarySources() >> [ s ]
            }
            GovernanceTier.getHierarchy() >>  [ tier ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    test_library: [:]
                ]
            ])

        when:
            LibraryLoader.doInject(config, script)
        then:
            1 * s.loadLibrary(script, "test_library", [:])
    }

    @WithoutJenkins
    def "Libraries can be loaded across library sources in a governance tier"(){
        setup:
            TemplateLibrarySource s1 = Mock{
                hasLibrary("libA") >> true
            }
            TemplateLibrarySource s2 = Mock{
                hasLibrary("libB") >> true
            }
            GovernanceTier tier = GroovyMock(global: true){
                getLibrarySources() >> [ s1, s2 ]
            }
            GovernanceTier.getHierarchy() >> [ tier ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    libA: [:],
                    libB: [:]
                ]
            ])

        when:
            LibraryLoader.doInject(config, script)
        then:
            1 * s1.loadLibrary(script, "libA", [:])
            0 * s1.loadLibrary(script, "libB", [:])
            1 * s2.loadLibrary(script, "libB", [:])
            0 * s2.loadLibrary(script, "libA", [:])
    }

    @WithoutJenkins
    def "Libraries can be loaded across library sources in different governance tiers"(){
        setup:
            TemplateLibrarySource s1 = Mock{
                hasLibrary("libA") >> true
            }
            TemplateLibrarySource s2 = Mock{
                hasLibrary("libB") >> true
            }
            GovernanceTier tier1 = Mock{
                getLibrarySources() >> [ s1 ]
            }
            GovernanceTier tier2 = GroovyMock(global:true){
                getLibrarySources() >> [ s2 ]
            }
            GovernanceTier.getHierarchy() >> [ tier1, tier2 ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    libA: [:],
                    libB: [:]
                ]
            ])
        when:
            LibraryLoader.doInject(config, script)
        then:
            1 * s1.loadLibrary(script, "libA", [:])
            0 * s1.loadLibrary(script, "libB", [:])
            1 * s2.loadLibrary(script, "libB", [:])
            0 * s2.loadLibrary(script, "libA", [:])
    }

    @WithoutJenkins
    def "library on more granular governance tier gets loaded"(){
        setup:
             TemplateLibrarySource s1 = Mock{
                hasLibrary("libA") >> true
            }
            TemplateLibrarySource s2 = Mock{
                hasLibrary("libA") >> true
            }
            GovernanceTier tier1 = Mock{
                getLibrarySources() >> [ s1 ]
            }
            GovernanceTier tier2 = GroovyMock(global:true){
                getLibrarySources() >> [ s2 ]
            }
            GovernanceTier.getHierarchy() >> [ tier1, tier2 ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    libA: [:]
                ]
            ])

        when:
            LibraryLoader.doInject(config, script)
        then:
            1 * s1.loadLibrary(script, "libA", [:])
            0 * s2.loadLibrary(script, "libA", [:])
    }

    @WithoutJenkins
    def "library loader correctly passes step config"(){
        setup:
            TemplateLibrarySource libSource = Mock{
                hasLibrary("libA") >> true
                hasLibrary("libB") >> true
            }
            GovernanceTier tier = GroovyMock(global:true){
                getLibrarySources() >> [ libSource ]
            }
            GovernanceTier.getHierarchy() >> [ tier ]

            // mock libraries to load
            TemplateConfigObject config = new TemplateConfigObject(config: [
                libraries: [
                    libA: [
                        fieldA: "A"
                    ],
                    libB: [
                        fieldB: "B"
                    ]
                ]
            ])

        when:
            LibraryLoader.doInject(config, script)
        then:
            1 * libSource.loadLibrary(script, "libA", [fieldA: "A"])
            1 * libSource.loadLibrary(script, "libB", [fieldB: "B"])
    }

    @WithoutJenkins
    def "steps configured via configuration file get loaded as default step implementation"(){
        setup:
            TemplateBinding binding = Mock()
            script.getBinding() >> binding
            StepWrapper s = GroovyMock(StepWrapper, global: true)
            StepWrapper.createDefaultStep(script, "test_step", [:]) >> s

            TemplateConfigObject config = new TemplateConfigObject(config: [
                steps: [
                    test_step: [:]
                ]
            ])
        when:
            LibraryLoader.doInject(config, script)
            LibraryLoader.doPostInject(config, script)
        then:
            1 * binding.setVariable("test_step", s)
            1 * logger.println("[JTE] Creating step test_step from the default step implementation.")
    }

    @WithoutJenkins
    def "warning issued when configured steps conflict with loaded library"(){
        setup:
            TemplateBinding binding = Mock{
                hasStep("test_step") >> true
                getStep("test_step") >> new StepWrapper(library: "libA")
            }
            script.getBinding() >> binding
            TemplateConfigObject config = new TemplateConfigObject(config: [
                steps: [
                    test_step: [:]
                ]
            ])
        when:
            LibraryLoader.doInject(config, script)
            LibraryLoader.doPostInject(config, script)
        then:
            1 * logger.println("[JTE] Warning: Configured step test_step ignored. Loaded by the libA Library.")
            0 * binding.setVariable(_ , _)
    }

    @WithoutJenkins
    def "template methods not implemented are Null Step"(){
        setup:
            Set<String> registry = new ArrayList()
            TemplateBinding binding = Mock(){
                setVariable(_, _) >> { args ->
                    registry << args[0]
                }
                hasStep(_) >> { String stepName ->
                    stepName in registry
                }
                getProperty("registry") >> registry
            }
            script.getBinding() >> binding
            StepWrapper s = Mock()
            StepWrapper s2 = GroovyMock(global: true)
            StepWrapper.createDefaultStep(script, "test_step1", [:]) >> s
            StepWrapper.createNullStep("test_step2", script) >> s2

            TemplateConfigObject config = new TemplateConfigObject(config: [
                steps: [
                    test_step1: [:]
                ],
                template_methods: [
                    test_step1: [:],
                    test_step2: [:]
                ]
            ])
        when:
            LibraryLoader.doInject(config, script)
            LibraryLoader.doPostInject(config, script)
        then:
            1 * StepWrapper.createDefaultStep(script, "test_step1", [:])
            1 * StepWrapper.createNullStep("test_step2", script)
            0 * StepWrapper.createNullStep("test_step1", script)
    }

}
