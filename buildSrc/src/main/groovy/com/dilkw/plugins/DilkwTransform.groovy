package com.dilkw.plugins

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.Format
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

class DilkwTransform extends Transform {
    // 构造⽅法
    DilkwTransform() {
    }
    // 对应的 task 名
    @Override
    String getName() {
        return 'dilkwTransform'
    }
    // 你要对那些类型的结果进⾏转换(是字节码还是资源⽂件？)
    @Override
    Set<QualifiedContent.ContentType>
    getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }
    // 适⽤范围包括什么(整个 project 还是别的什么？)
    @Override
    Set<? super QualifiedContent.Scope> getScopes()
    {
        return TransformManager.SCOPE_FULL_PROJECT
    }
    @Override
    boolean isIncremental() {
        return false
    }
    // 具体的「转换」过程
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException,
    InterruptedException, IOException {
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        inputs.each {
            // jarInputs：各个依赖所编译成的 jar ⽂件
            it.jarInputs.each {
                File dest =
                        outputProvider.getContentLocation(it.name,
                                it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.file, dest)
            }
            it.directoryInputs.each {
                File dest = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.file, dest)
            }
        }
    }
}
