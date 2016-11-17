import org.bbop.apollo.FeatureType
import org.bbop.apollo.Role
import org.bbop.apollo.UserService
import org.bbop.apollo.sequence.SequenceTranslationHandler

class BootStrap {

    def sequenceService
    def configWrapperService
    def grailsApplication
    def featureTypeService
    def domainMarshallerService
    def proxyService
    def userService
    def phoneHomeService


    def init = { servletContext ->
        log.info "Initializing..."
        def dataSource = grailsApplication.config.dataSource
        log.info "Datasource"
        log.info "Url: ${dataSource.url}"
        log.info "Driver: ${dataSource.driverClassName}"
        log.info "Dialect: ${dataSource.dialect}"

        domainMarshallerService.registerObjects()
        proxyService.initProxies()

        phoneHomeService.pingServer("start")

        SequenceTranslationHandler.spliceDonorSites.addAll(configWrapperService.spliceDonorSites)
        SequenceTranslationHandler.spliceAcceptorSites.addAll(configWrapperService.spliceAcceptorSites)

        if(FeatureType.count==0){
            featureTypeService.stubDefaultFeatureTypes()
        }

        if(Role.count==0){
            def userRole = new Role(name: UserService.USER).save()
            userRole.addToPermissions("*:*")
            userRole.removeFromPermissions("cannedComments:*")
            userRole.removeFromPermissions("availableStatus:*")
            userRole.removeFromPermissions("featureType:*")
            def adminRole = new Role(name: UserService.ADMIN).save()
            adminRole.addToPermissions("*:*")
        }

        def admin = grailsApplication.config?.apollo?.admin
        if(admin){
            userService.registerAdmin(admin.username,admin.password,admin.firstName,admin.lastName)
        }

        Integer timer =   24 * 60 * 60 * 1000
        new Timer().schedule({
//            phoneHomeService.pingServer("running",["numUsers":User.count.toString(),"numAnnotations": Feature.count.toString(),"numOrganisms": org.bbop.apollo.Organism.count.toString()])
            phoneHomeService.pingServer("running")
            // phone home once a day
        } as TimerTask, 1000, timer)

    }
    def destroy = {
        phoneHomeService.pingServer("stop")
    }
}
