import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("kotlin-kapt")
    id("io.fabric")
    // always after android plugin
    id("com.getkeepsafe.dexcount")
}

android {
    compileSdkVersion(27)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(26)
        versionCode = 42
        versionName = "Kotlin DSL"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    val ankoVersion = "0.10.4"
    val archLifecycleVersion = "1.1.1"
    val roomVersion = "1.1.1"
    val supportRunner = "1.0.1"
    val supportLibVersion = "27.1.1"
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation ("com.android.support:appcompat-v7:$supportLibVersion")
    implementation ("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.android.support:design:$supportLibVersion")
    // okhttp3
    compile ("com.squareup.okhttp3:okhttp:3.10.0")
    compile ("com.squareup.okhttp3:logging-interceptor:3.10.0")
    // Pleasant Android application development
    compile ("org.jetbrains.anko:anko-common:$ankoVersion")
    compile ("org.jetbrains.anko:anko-design:$ankoVersion")
    implementation ("com.google.code.gson:gson:2.8.2")
    compile ("android.arch.persistence.room:runtime:$roomVersion")
    kapt ("android.arch.persistence.room:compiler:$roomVersion")
    androidTestImplementation ("android.arch.persistence.room:testing:$roomVersion")
    // Lifecycle components
    implementation ("android.arch.lifecycle:extensions:$archLifecycleVersion")
    kapt ("android.arch.lifecycle:compiler:$archLifecycleVersion")
    // Fabric integration
    compile("com.crashlytics.sdk.android:crashlytics:2.9.1@aar") { isTransitive = true }
    // Retrofit
    compile ("com.squareup.retrofit2:retrofit:2.3.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.3.0")
    // Card View
    compile(group = "com.android.support", name = "cardview-v7", version = "27.1.1")
    // Testing
    testCompile ("com.android.support.test:runner:$supportRunner")
    testCompile ("org.assertj:assertj-core:1.7.1")
    testCompile ("org.mockito:mockito-core:2.13.0")
    testImplementation ("junit:junit:4.12")
    androidTestImplementation ("com.android.support.test:runner:$supportRunner")
    androidTestImplementation ("com.android.support.test.espresso:espresso-core:3.0.2")

}