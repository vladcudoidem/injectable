plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    val automaticRelease = false
    publishToMavenCentral(automaticRelease)
    signAllPublications()

    // Call configure() in each project.

    pom {
        // Configure the name, description and inceptionYear in each project.

        url = "https://github.com/vladcudoidem/injectable"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/MIT"
            }
        }
        developers {
            developer {
                id = "vladcudoidem"
                name = "Alexandru-Vlad Vamos"
                url = "https://github.com/vladcudoidem"
            }
        }
        scm {
            url = "https://github.com/vladcudoidem/injectable"
            connection = "scm:git:git://github.com/vladcudoidem/injectable.git"
            developerConnection = "scm:git:ssh://git@github.com/vladcudoidem/injectable.git"
        }
    }
}
