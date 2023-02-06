attribute vec3 attPosition;
attribute vec3 attNormal;
attribute vec2 attUV;
attribute vec3 attTangent;
attribute vec3 attBigTangent;

uniform mat4 u_ModelViewProjectMat;
uniform mat4 u_ModelMat;
uniform mat3 u_NormalWorldMat;

varying vec3 v_WorldPosition;
varying vec3 v_WorldNormal;
#ifdef AE_USE_NORMAL_TEXTURE
varying vec3 v_WorldTangent;
varying vec3 v_WorldBinormal;
#endif
varying vec2 v_Texcoord;

#ifdef AE_USE_BONES
uniform mat4 u_BoneMates[48];
attribute vec4 attBoneIds;
attribute vec4 attWeights;
void simulationBoneTransform(inout vec4 position, inout vec3 normal, inout vec3 tangent, inout vec3 biNormal)
{
    mat4 boneMat = u_BoneMates[int(attBoneIds.x)] * attWeights.x;
    boneMat += u_BoneMates[int(attBoneIds.y)] * attWeights.y;
    boneMat += u_BoneMates[int(attBoneIds.z)] * attWeights.z;
    boneMat += u_BoneMates[int(attBoneIds.w)] * attWeights.w;
    position =  boneMat * position;
    normal   = (boneMat * vec4(normal, 0.0)).xyz;
#ifdef AE_USE_NORMAL_TEXTURE
    tangent  = (boneMat * vec4(tangent, 0.0)).xyz;
    biNormal = (boneMat * vec4(biNormal, 0.0)).xyz;;
#endif
}
#endif

void main()
{
    vec4 finalPosition = vec4(attPosition, 1.0);
    vec3 finalNormal   = attNormal;
    vec3 finalTangent  = attTangent;
    vec3 finalBinormal = attBigTangent;
#ifdef AE_USE_BONES
    simulationBoneTransform(finalPosition, finalNormal, finalTangent, finalBinormal);
#endif
    
#ifdef AE_USE_NORMAL_TEXTURE
    v_WorldTangent  = u_NormalWorldMat * finalTangent;
    v_WorldBinormal = u_NormalWorldMat * finalBinormal;
#endif
    v_WorldNormal   = u_NormalWorldMat * finalNormal;
    v_WorldPosition = (u_ModelMat * finalPosition).xyz;
    v_Texcoord      = attUV;
    v_Texcoord.y    = 1.0 - v_Texcoord.y;
    
    gl_Position = u_ModelViewProjectMat * finalPosition;
}
