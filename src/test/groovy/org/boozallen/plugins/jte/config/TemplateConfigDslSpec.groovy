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

import spock.lang.*

class TemplateConfigDslSpec extends Specification {

    def 'Empty Config File'(){
        setup:
            String config = ""
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.config == [:]
            configObject.merge.isEmpty()
            configObject.override.isEmpty()
    }

    def 'Flat Keys Configuration'(){
        setup:
            String config = """
            a = 3
            b = "hi"
            c = true
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.config == [
                a: 3,
                b: "hi",
                c: true
            ]
    }

    def 'Nested Keys Configuration'(){
        setup:
            String config = """
            random = "hi"
            application_environments{
                dev{
                    field = true
                }
                test{
                    field = false
                }
            }
            blah{
                another{
                    field = "hey"
                }
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.config == [
                random: "hi",
                application_environments: [
                    dev: [
                        field: true
                    ],
                    test: [
                        field: false
                    ]
                ],
                blah: [
                    another: [
                        field: "hey"
                    ]
                ]
            ]
    }

    def 'One Merge First Key'(){
        setup:
            String config = """
            application_environments{
                merge = true
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.merge == [ "application_environments" ] as Set
    }

    def 'One Merge Nested Key'(){
        setup:
            String config = """
            application_environments{
                dev{
                    merge = true
                }
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.merge == [ "application_environments.dev" ] as Set
    }

    def 'Multi-Merge'(){
        setup:
            String config = """
            application_environments{
                dev{
                    merge = true
                }
                test{
                    merge = true
                }
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.merge == [ "application_environments.dev", "application_environments.test" ] as Set
    }

    def 'One Override First Key'(){
        when:
            String config = """
            application_environments{
                override = true
            }
            """
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.override == [ "application_environments" ] as Set
    }

    def 'One Override Nested Key'(){
        when:
            String config = """
            application_environments{
                dev{
                    override = true
                }
            }
            """
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.override == [ "application_environments.dev" ] as Set
    }

    def 'Multi-Override'(){
        setup:
            String config = """
            application_environments{
                dev{
                    override = true
                }
                test{
                    override = true
                }
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.override == [ "application_environments.dev", "application_environments.test" ] as Set
    }

    def 'File Access Throws Security Exception'(){
        setup:
            String config = """
            password = new File("/etc/passwd").text
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            thrown(SecurityException)
    }

    def "nested blank entry results in empty hashmap"(){
        setup:
            String config = """
            application_environments{
                dev
            }
            """
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.getConfig() == [
                application_environments: [
                    dev: [:]
                ]
            ]
    }

    def "root blank entry results in empty hashmap"(){
        setup:
            String config = "field"
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.getConfig() == [ field: [:] ]
    }

    def "merge key when not true is not added to list"(){
        setup:
            String config = "a{ merge = false }"
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.getConfig() == [ a: [ merge: false ] ]
            configObject.merge == [] as Set
    }

    def "override key when not true is not added to list"(){
        setup:
            String config = "a{ override = false }"
        when:
            TemplateConfigObject configObject = TemplateConfigDsl.parse(config)
        then:
            configObject.getConfig() == [ a: [ override: false ] ]
            configObject.override == [] as Set
    }

}
