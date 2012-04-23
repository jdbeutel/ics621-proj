application {
    title = 'HelloProcessing'
    startupGroups = ['helloProcessing']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "helloProcessing"
    'helloProcessing' {
        model      = 'helloprocessing.HelloProcessingModel'
        view       = 'helloprocessing.HelloProcessingView'
        controller = 'helloprocessing.HelloProcessingController'
    }

}
