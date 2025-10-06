# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Apache POI classes to prevent reflection issues
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.apache.commons.** { *; }
-keep class schemaorg_apache_xmlbeans.** { *; }

# Keep reflection methods
-keepclassmembers class * {
    java.lang.reflect.Method invoke*(...);
}

# Keep GC suppression related methods
-keep class * implements java.lang.reflect.InvocationHandler

# Keep all classes that might be used with reflection
-keepclassmembers class ** {
    @org.apache.poi.util.Internal volatile <fields>;
}

# Keep XML parsing related classes
-keep class javax.xml.** { *; }
-keep class org.xml.sax.** { *; }
