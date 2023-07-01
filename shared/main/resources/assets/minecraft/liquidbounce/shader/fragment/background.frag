#define WAVES 5.
#ifdef GL_ES
precision mediump float;
#endif

//precision highp float;

uniform float iTime;
uniform vec2 iMouse;
uniform vec2 iResolution;

void main( void ) {
    vec2 uv = gl_FragCoord.xy/iResolution.xy;

    // Time varying pixel color
    vec3 col = 0.5 + 0.5*cos(iTime+uv.xyx * 2.+vec3(0,2,4));

    // Output to screen
    gl_FragColor = vec4(col,1.0);
}