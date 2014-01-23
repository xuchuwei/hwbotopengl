package org.hwbot.bench.gpu.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class CustomShaderLoader {

    final static String VERT = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +

            "uniform mat4 u_projTrans;\n" + " \n" + "varying vec4 vColor;\n" + "varying vec2 vTexCoord;\n" +

            "void main() {\n" + "   vColor = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + "   vTexCoord = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}";

    // This will be dumped to System.out for clarity
    final static String FRAG =
    // GL ES specific stuff
    "#ifdef GL_ES\n" //
            + "#define LOWP lowp\n" //
            + "precision mediump float;\n" //
            + "#else\n" //
            + "#define LOWP \n" //
            + "#endif\n"
            + //
            "//texture 0\n" + "uniform sampler2D u_texture;\n" + "\n"
            + "//our screen resolution, set from Java whenever the display is resized\n"
            + "uniform vec2 resolution;\n" + "\n" + "//\"in\" attributes from our vertex shader\n"
            + "varying LOWP vec4 vColor;\n"
            + "varying vec2 vTexCoord;\n" + "\n"
            + "//RADIUS of our vignette, where 0.5 results in a circle fitting the screen\n"
            + "const float RADIUS = 0.75;\n" + "\n" + "//softness of our vignette, between 0.0 and 1.0\n" + "const float SOFTNESS = 0.45;\n"
            + "\n"
            + "//sepia colour, adjust to taste\n" + "const vec3 SEPIA = vec3(1.2, 1.0, 0.8); \n" + "\n" + "void main() {\n"
            + "   //sample our texture\n"
            + "   vec4 texColor = texture2D(u_texture, vTexCoord);\n" + "       \n" + "   //1. VIGNETTE\n" + "   \n"
            + "   //determine center position\n"
            + "   vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);\n" + "   \n"
            + "   //determine the vector length of the center position\n"
            + "   float len = length(position);\n" + "   \n"
            + "   //use smoothstep to create a smooth vignette\n"
            + "   float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);\n" + "   \n"
            + "   //apply the vignette with 50% opacity\n"
            + "   texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);\n" + "       \n" + "   //2. GRAYSCALE\n"
            + "   \n"
            + "   //convert to grayscale using NTSC conversion weights\n" + "   float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));\n"
            + "   \n"
            + "   //3. SEPIA\n" + "   \n" + "   //create our sepia tone from some constant value\n"
            + "   vec3 sepiaColor = vec3(gray) * SEPIA;\n"
            + "       \n" + "   //again we'll use mix so that the sepia effect is at 75%\n"
            + "   texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);\n"
            + "       \n" + "   //final colour, multiplied by vertex colour\n" + "   gl_FragColor = texColor * vColor;\n" + "}";

    static final public ShaderProgram createShader() {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT, FRAG);
        if (!shader.isCompiled()) {
            System.out.println("Shader not compiled: " + shader.getLog());
            Gdx.app.exit();
        } else
            Gdx.app.log("shader compiled", shader.getLog());
        return shader;
    }
}
