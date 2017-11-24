parallelExecution in Test := false

initialize ~= { _ =>
  System.setProperty( "config.file", "conf/application.conf" )
}
