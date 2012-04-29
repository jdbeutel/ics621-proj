package amortizedcola

application(title: 'AmortizedCola',
  preferredSize: [1024, 768],   // XGA in the hope that the projector is this size
  pack: true,
  //location: [50,50],
  locationByPlatform:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]) {
    // add content here
    label('Content Goes Here') // delete me
    processing(model.pApplet)
}
