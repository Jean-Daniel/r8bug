// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
  // avoid warning about duplicate plugin applying by applying plugin here
  id("com.android.application") apply false

  kotlin("android") apply false
}

