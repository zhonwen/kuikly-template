plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.11.1").apply(false)
    id("com.android.library").version("8.11.1").apply(false)
    kotlin("android").version("2.0.21").apply(false)
    kotlin("multiplatform").version("2.0.21").apply(false)
    id("com.google.devtools.ksp").version("2.0.21-1.0.27").apply(false)

}