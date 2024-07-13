plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.storyappsub2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.storyappsub2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Glide: Image loading and caching library for Android
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    // Kotlin Parcelize: Simplifies Parcelable implementation
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:1.5.10")

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    // Retrofit: Type-safe HTTP client for Android and Java
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp: HTTP & HTTP/2 client for Android and Java applications
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // AndroidX Lifecycle: ViewModel and LiveData for lifecycle-aware components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")

    // RecyclerView: A flexible view for providing a limited window into a large data set
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // CardView: Implements the Material Design CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // AndroidX DataStore: Replaces SharedPreferences for data storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")

    // AndroidX Activity and Fragment KTX: Adds Kotlin extensions for Activities and Fragments
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.7.0")

    // Paging Library: Helps you load and display small chunks of data at a time
    implementation("androidx.paging:paging-runtime:3.1.1")

    // Kotlin Coroutines: Library for coroutines support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")

    // Google Play Services: Maps and Location services
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // JUnit: A simple framework to write repeatable tests
    testImplementation("junit:junit:4.13.2")

    // AndroidX Test: Provides Android instrumentation testing support
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Testing dependencies
    // InstantTaskExecutorRule for AndroidX
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

    // InstantTaskExecutorRule for AndroidX
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

    // Mockito: Mocking framework for unit tests
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
}
