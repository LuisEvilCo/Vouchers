// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven ("https://maven.fabric.io/public")

    }
    dependencies {
        classpath ("com.android.tools.build:gradle:3.2.1")
        classpath(kotlin("gradle-plugin", version = "1.3.10"))
        classpath ("io.fabric.tools:gradle:1.25.4")
        classpath ("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.2") // output : ${buildDir}/outputs/dexcount/${variant}

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven ("https://maven.fabric.io/public")
    }
}

task<Delete>("clean") {
    group = "build"
    delete("${rootProject.buildDir}")
}
