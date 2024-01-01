package com.dilkw.plugins

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.dsl.DynamicFeatureExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class DilkwGradlePlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        def extension = target.extensions.create("dilkwExtension", DilkwGradleExtension)
        // 注意这里需要通过afterEvaluate将扩展中执行的代码放到扩展使用后再执行
        target.afterEvaluate {
            println "hello ${extension.name}"
        }
        def dilkwTransformExtensions = target.extensions.getByType(BaseExtension)
        dilkwTransformExtensions.registerTransform(new DilkwTransform())
    }
}