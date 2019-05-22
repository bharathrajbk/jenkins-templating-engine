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
package org.boozallen.plugins.jte.utils

import jenkins.scm.api.SCMFile
import jenkins.scm.api.SCMFileSystem
import org.boozallen.plugins.jte.Utils
import spock.lang.Specification


class FileSpec extends Specification {

    def setup(){

    }


    def "Utils.FileSystemWrapper.getFileContents with null SCMFileSystem"(){

        when:"getFileContents is called with a null SCMFileSystem and non-empty string for path"
        String retVal = Utils.FileSystemWrapper.getFileContents("pipeline_config.groovy", null)

        then:"a null is returned for SCMFile"
        null == retVal

    }

    def "Utils.FileSystemWrapper.getFileContents where SCMFileSystem says f.exists() == false, logMissingFile:true -> log missing file"(){
        given:
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> false

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)

        String retVal = ""

        when:
        retVal = Utils.FileSystemWrapper.getFileContents(filePath, fs, log, true)

        then:
        null == retVal
        0 * childFile.isFile()
        1 * printStream.println(_)
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper.getFileContents where SCMFileSystem says !f.exists(),  logMissingFile: false -> no log"(){
        given:
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> false

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)

        String retVal = ""

        when:
        retVal = Utils.FileSystemWrapper.getFileContents(filePath, fs, log, false)

        then:
        null == retVal
        0 * childFile.isFile()
        0 * printStream.println(_)
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper.getFileContents where SCMFileSystem says f.isFile() == false"(){
        given:
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> true
        1 * childFile.isFile() >> false

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)

        String retVal = ""

        when:
        retVal = Utils.FileSystemWrapper.getFileContents(filePath, fs, log, true)

        then:
        null == retVal

        1 * printStream.println(_)
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper.getFileContents where SCMFileSystem has valid file"(){
        given:
        String fileContents = "woot, a file"
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> true
        1 * childFile.isFile() >> true
        1 * childFile.contentAsString() >> fileContents

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)

        String retVal = ""

        when:
        retVal = Utils.FileSystemWrapper.getFileContents(filePath, fs, log, true)

        then:
        fileContents == retVal

        0 * printStream.println(_)
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper.getFileContents where SCMFileSystem has valid file with log.desc"(){
        given:
        String fileContents = "woot, a file"
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> true
        1 * childFile.isFile() >> true
        1 * childFile.contentAsString() >> fileContents

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)
        log.desc = "[JTE] description"

        String retVal = ""

        when:
        retVal = Utils.FileSystemWrapper.getFileContents(filePath, fs, log, true)

        then:
        fileContents == retVal

        1 * printStream.println(_) // log.desc
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper#getFileContents where SCMFileSystem has valid file"(){
        given:
        String fileContents = "woot, a file"
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> true
        1 * childFile.isFile() >> true
        1 * childFile.contentAsString() >> fileContents

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)
        Utils.FileSystemWrapper fsw = new Utils.FileSystemWrapper(log: log, fs: fs)

        String retVal = ""

        when:
        retVal = fsw.getFileContents(filePath)

        then:
        fileContents == retVal

        0 * printStream.println(_)
        1 * fs.close()

    }

    def "Utils.FileSystemWrapper#getFileContents where !f.exists -> log missing file by default"(){
        given:
        String filePath = "pipeline_config.groovy"
        SCMFile childFile = GroovyMock(SCMFile)
        1 * childFile.exists() >> false

        SCMFileSystem fs = GroovyMock(SCMFileSystem)
        1 * fs.asBoolean() >> true
        1 * fs.child("pipeline_config.groovy") >> childFile

        PrintStream printStream = Mock(PrintStream)

        Utils.Logger log = new Utils.Logger(printStream: printStream)
        Utils.FileSystemWrapper fsw = new Utils.FileSystemWrapper(log: log, fs: fs)

        String retVal = ""

        when:
        retVal = fsw.getFileContents(filePath)

        then:
        null == retVal
        0 * childFile.isFile()
        1 * printStream.println(_)
        1 * fs.close()

    }
}
