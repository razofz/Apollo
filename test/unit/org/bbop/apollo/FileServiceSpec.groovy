package org.bbop.apollo

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(FileService)
class FileServiceSpec extends Specification {

    private final String FINAL_DIRECTORY = "test/unit/resources/archive_tests/"

    File fileA = new File(FINAL_DIRECTORY+"/a.txt")
    File fileB = new File(FINAL_DIRECTORY+"/b.txt")

    def setup() {
    }

    def cleanup(){
        fileA.delete()
        fileB.delete()
    }

    void "handle tar.gz decompress"() {

        given: "a tar.gz file"
        File inputFile = new File(FINAL_DIRECTORY + "/no_symlinks.tgz" )
        println "input file ${inputFile} ${inputFile.exists()}"
        println "current working directory  ${new File(".").absolutePath}"

        when: "we expand it"
        List<String> fileNames = service.decompressTarArchive(inputFile,FINAL_DIRECTORY)
        println "fileNames ${fileNames.join(",")}"

        then: "we should have the right file"
        assert fileA.exists()
        assert fileB.exists()


    }

    void "handle symlinks"() {

        given: "a tar.gz file"
        File inputFile = new File(FINAL_DIRECTORY + "/symlinks.tgz" )
        println "current working directory  ${new File(".").absolutePath}"

        when: "we expand it"
        List<String> fileNames = service.decompressTarArchive(inputFile,FINAL_DIRECTORY)
        println "fileNames should have a symlink in it ${fileNames.join(",")}"

        then: "we should have the right file"
        assert fileA.exists()
        assert fileB.exists()


    }



}
