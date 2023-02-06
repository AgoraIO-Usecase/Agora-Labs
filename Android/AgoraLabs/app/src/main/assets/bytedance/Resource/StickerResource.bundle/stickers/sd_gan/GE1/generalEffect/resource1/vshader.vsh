precision highp float;

attribute vec3 attPosition;
attribute vec2 attUV;

varying highp vec2 textureCoordinate;

void main() {
    gl_Position = vec4(attPosition.xy, 0.0, 1.0);
    textureCoordinate = attUV.xy;
}

