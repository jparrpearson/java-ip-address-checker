# java-ip-address-checker

This is a Java application that checks the external IP address using an online service.  Once the IP address has been found it is stored in a local file and optionally emailed to an email address.

This is useful for being notified when your IP address changes so that any services that must connect to your external IP address, such as Windows Remote Desktop, can be used.

## Build

This project uses [Gradle](http://gradle.org/), and the following tasks assume that it is already installed.

### Resolve Dependencies

If editing in Eclipse, first resolve dependencies and place them in the `lib` folder (ensure that all needed dependencies are also listed in the `.classpath` file, and run this task before opening the project in Eclipse):
```
gradle prepare
```

### Packaging the Application

In order to build the application and package it in a distributable ZIP file (can then be found under the `build/distributions` folder):
```
gradle dist
```

## Usage

After packaging the application, install and configure as follows:

1. Unzip the distributable package to a folder of your choice
2. Update the options, as described below, in the properties file (`conf/ip-address-checker.properties`)
3. Run the application
```
java -jar java-ip-address-checker-1.0.jar
```

### Options

| Property Name | Description |
| --- | --- |
| services | A comma-separated list of online services to retrieve the external IP address from.  Each service will be tried in the specified order until one succeeds. |
| mail.enabled | If `true`, will email the configured email address with the IP address if it is different from the last run. |
| mail.smtp.host | The SMTP server host. |
| mail.smtp.port | The SMTP server port. |
| mail.smtp.username | The SMTP server username. |
| mail.smtp.password | The SMTP server password. |
| mail.to | The to email to set on the email (to send the IP address to). |
| mail.from | The from email address to set on the email. |
| mail.subject | The subject to set on the email. |
| last.address | The last IP address that was found and saved.  An email is only sent if the newly found IP address is different from the last one. |

Note that most email providers have limits on the use of their SMTP mail servers.  So it is not recommended to run the application too often.  A typical use case would be to install the application and make use of a scheduler, such as the Windows Task Scheduler, to run the application periodically.  There is a batch file, `run.bat`, included for this purpose.

## License

Copyright (c) 2016 Jeremy Parr-Pearson

The MIT License (MIT)
