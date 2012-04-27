application {
    title = 'AmortizedCola'
    startupGroups = ['amortizedCola']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "amortizedCola"
    'amortizedCola' {
        model      = 'amortizedcola.AmortizedColaModel'
        view       = 'amortizedcola.AmortizedColaView'
        controller = 'amortizedcola.AmortizedColaController'
    }

}
