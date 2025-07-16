plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.diyas.uts_ppm_2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.diyas.uts_ppm_2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("androidx.room:room-runtime:2.6.1")
    androidTestImplementation(libs.testng)
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation ("com.prolificinteractive:material-calendarview:1.4.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")



}