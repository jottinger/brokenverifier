/*
 * This code is licensed under the Apache Source License, v2.0.
 * See http://www.apache.org/licenses/LICENSE-2.0
 */

package org.javachannel.examples.brokenverifier;

import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class VerifierTest {
    class MyClassLoader extends ClassLoader {
        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

    @Test
    public void generateComparator() {
        final String slashName = "test/Verifier";
        final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_7, ACC_PUBLIC | ACC_SYNTHETIC, slashName, null, "java/util/HashSet", null);

        Label lOtherSuper = new Label();
        Label lDoSomething = new Label();

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitFrame(F_NEW, 1, new Object[] { UNINITIALIZED_THIS }, 0, EMPTY_OBJECT_ARRAY);
        mv.visitJumpInsn(GOTO, lOtherSuper);

        mv.visitFrame(F_NEW, 1, new Object[]{ UNINITIALIZED_THIS }, 0, EMPTY_OBJECT_ARRAY);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "()V", false);

        mv.visitFrame(F_NEW, 1, new Object[] { slashName }, 0, EMPTY_OBJECT_ARRAY);
        mv.visitLabel(lDoSomething);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashSet", "size", "()I", false);
        mv.visitInsn(POP);
        mv.visitInsn(RETURN);

        mv.visitFrame(F_NEW, 1, new Object[]{ UNINITIALIZED_THIS }, 0, EMPTY_OBJECT_ARRAY);
        mv.visitLabel(lOtherSuper);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(BIPUSH, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "(I)V", false);
        mv.visitJumpInsn(GOTO, lDoSomething);

        mv.visitMaxs(3, 1);
        mv.visitEnd();
        cw.visitEnd();

        byte[] b = cw.toByteArray();

        MyClassLoader cl=new MyClassLoader();
        Class c=cl.defineClass(slashName.replace('/', '.'), b);
        Method[] m=c.getMethods();

    }
}
