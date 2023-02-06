#ifdef GL_ES
#extension GL_OES_standard_derivatives : enable
precision highp float;
#endif

#if defined(_AE_USE_DIRECTIONAL_LIGHTING_) || defined(_AE_USE_POINT_LIGHTING_) || defined(_AE_USE_SPOT_LIGHTING_)
#define AE_USE_LIGHTING
#endif


#if defined(AE_USE_METALLIC_TEXTURE) || defined(AE_USE_ROUGHNESS_TEXTURE) || defined(AE_USE_OCCLUSION_TEXTURE) || defined(AE_USE_OPACITY_TEXTURE)
#define AE_USE_MROO_TEXTURE
#endif

#ifdef _AE_USE_DIRECTIONAL_LIGHTING_
#define MAX_DIRECTIONAL_LIGHTS_NUM 1
uniform float _AE_DIRECTIONAL_LIGHTS_ENABLED_[MAX_DIRECTIONAL_LIGHTS_NUM];
uniform vec3  _AE_DIRECTIONAL_LIGHTS_DIRECTION_[MAX_DIRECTIONAL_LIGHTS_NUM];
uniform vec3  _AE_DIRECTIONAL_LIGHTS_COLOR_[MAX_DIRECTIONAL_LIGHTS_NUM];
uniform float _AE_DIRECTIONAL_LIGHTS_INTENSITY_[MAX_DIRECTIONAL_LIGHTS_NUM];
uniform mat4  _AE_DIRECTIONAL_LIGHT0_SHADOW_MATRIX_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_ENABLED_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_BIAS_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_STRENGTH_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFT_;
uniform float _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFTNESS_;
uniform sampler2D _AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_;
uniform vec2  _AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_SIZE_;
#endif

#ifdef _AE_USE_POINT_LIGHTING_
#define MAX_POINT_LIGHTS_NUM 1
uniform float _AE_POINT_LIGHTS_ENABLED_[MAX_POINT_LIGHTS_NUM];
uniform vec3  _AE_POINT_LIGHTS_POSITION_[MAX_POINT_LIGHTS_NUM];
uniform vec3  _AE_POINT_LIGHTS_COLOR_[MAX_POINT_LIGHTS_NUM];
uniform float _AE_POINT_LIGHTS_INTENSITY_[MAX_POINT_LIGHTS_NUM];
uniform float _AE_POINT_LIGHTS_ATTENUATION_RANGE_INV_[MAX_POINT_LIGHTS_NUM];
#endif

#ifdef _AE_USE_SPOT_LIGHTING_
#define MAX_SPOT_LIGHTS_NUM 1
uniform float _AE_SPOT_LIGHTS_ENABLED_[MAX_SPOT_LIGHTS_NUM];
uniform vec3  _AE_SPOT_LIGHTS_DIRECTION_[MAX_SPOT_LIGHTS_NUM];
uniform vec3  _AE_SPOT_LIGHTS_POSITION_[MAX_SPOT_LIGHTS_NUM];
uniform vec3  _AE_SPOT_LIGHTS_COLOR_[MAX_SPOT_LIGHTS_NUM];
uniform float _AE_SPOT_LIGHTS_INTENSITY_[MAX_SPOT_LIGHTS_NUM];
uniform float _AE_SPOT_LIGHTS_INNER_ANGLE_COS_[MAX_SPOT_LIGHTS_NUM];
uniform float _AE_SPOT_LIGHTS_OUTER_ANGLE_COS_[MAX_SPOT_LIGHTS_NUM];
uniform float _AE_SPOT_LIGHTS_ATTENUATION_RANGE_INV_[MAX_SPOT_LIGHTS_NUM];
#endif

uniform vec3  u_Diffuse;
uniform float u_Roughness;
uniform float u_Metallic;
uniform float u_Alpha;

uniform vec3  u_CameraPosInWorld;
#ifdef AE_USE_DIFFUSE_TEXTURE
uniform sampler2D u_DiffuseTex;
#endif
#ifdef AE_USE_NORMAL_TEXTURE
uniform sampler2D u_NormalTex;
#endif

#ifdef AE_USE_MROO_TEXTURE
uniform sampler2D u_MROOTex; //  Metallic/Roughness/Occlusion/Opacity
#endif

#ifdef AE_USE_EMISSIVE_TEXTURE
uniform float u_Emissive;
uniform sampler2D u_EmissiveTex;
#endif

#ifdef AE_USE_IBL
uniform sampler2D u_IrradianceTex;
uniform sampler2D u_RadianceTex;
uniform sampler2D u_LutTex;
#endif

varying vec3 v_WorldPosition;
varying vec3 v_WorldNormal;
#ifdef AE_USE_NORMAL_TEXTURE
varying vec3 v_WorldTangent;
varying vec3 v_WorldBinormal;
#endif
varying vec2 v_Texcoord;
const float PI = 3.1415926;
#define SAMPLE_SIZE 9

vec3 LinearColor(vec3 col) {
    return pow(col, vec3(0.45454545));
}

vec3 NonLinearColor(vec3 col) {
    return pow(col, vec3(2.2));
}

vec3 FresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (vec3(1.0) - F0) * pow(1.0 - cosTheta, 5.0);
}

vec2 CalculateCoordsWithLatLong(vec3 D) {
    return vec2((atan(D.x, D.z) / PI + 1.0) * 0.5, (acos(D.y) / PI));
}

#ifdef AE_USE_IBL

vec3 Irradiance(vec3 worldNormal, sampler2D irradianceSamper) {
    vec2 irradianceCoords = CalculateCoordsWithLatLong(worldNormal);
    return texture2D(irradianceSamper, irradianceCoords).rgb;
}
vec3 Radiance(vec3 worldReflect, sampler2D radianceSamper) {
    vec2 radianceCoords = CalculateCoordsWithLatLong(worldReflect);
    return texture2D(radianceSamper, radianceCoords).rgb;
}
vec2 Lut(float NdotV, float roughness, sampler2D lutSamper) {
    return texture2D(lutSamper, vec2(roughness, 1.0 - NdotV)).rg;
}
vec3 IBL(vec3 normal,
         vec3 viewDir,
         vec3 albedo,
         float roughness,
         float metallic,
         sampler2D irradianceSamper,
         sampler2D radianceSamper,
         sampler2D lutSamper) {
    float NdotV = max(0.0, dot(normal, viewDir));
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);
    vec3 F    = FresnelSchlick(NdotV, F0);
    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;
    vec3 irradiance = Irradiance(normal, irradianceSamper);
    vec3 r = normalize(reflect(-viewDir, normal));
    vec3 radiance = Radiance(r, radianceSamper);
    vec2 envBRDF = Lut(NdotV, roughness, lutSamper);
    vec3 specular = radiance * (kS * envBRDF.x + envBRDF.y);
    vec3 ambient = (kD * irradiance * albedo + specular);
    return ambient;
}
#endif

#ifdef AE_USE_LIGHTING

float DistributionGGX(float NdotH, float roughness)
{
    float a      = roughness * roughness;
    float a2     = a * a;
    float NdotH2 = NdotH * NdotH;
    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;
    return nom / denom;
}

float DistributionBeckmann(float NdotH, float roughness)
{
    float m = roughness * roughness;
    float m2 = m * m;
    float NdotH2 = NdotH * NdotH;
    return exp((NdotH2 - 1.0) / (m2 * NdotH2)) / (PI * m2 * NdotH2 * NdotH2);
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r * r) / 8.0;
    float denom = NdotV * (1.0 - k) + k;
    return NdotV / denom;
}

float GeometrySmith(float NdotV, float NdotL, float roughness)
{
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);
    return ggx1 * ggx2;
}

vec3 PBRLighting(float NdotL, float NdotH, float NdotV, float HdotV, vec3 albedo, float roughness, float metallic) {
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, albedo, metallic);
    float NDF = DistributionGGX(NdotH, roughness);
    float G   = GeometrySmith(NdotV, NdotL, roughness);
    vec3 F    = FresnelSchlick(HdotV, F0);
    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;
    vec3 nominator    = NDF * G * F;
    float denominator = 4.0 * NdotV * NdotL + 0.001;
    vec3 brdf = nominator / denominator;
    vec3 Lo = (kD * NdotL * albedo + brdf * NdotL);
    return Lo;
}

float UnpackFloatFromVec4i(const vec4 value)
{
    const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
    return(dot(value, bitSh));
}

float Shadowing(vec3 worldPosition, float NdotL) {
    if (_AE_DIRECTIONAL_LIGHT0_SHADOW_ENABLED_ < 1.0)
        return 1.0;
    vec4 projectPosition = _AE_DIRECTIONAL_LIGHT0_SHADOW_MATRIX_ * vec4(worldPosition, 1.0);
    vec3 shadowCoord = projectPosition.xyz / projectPosition.w;
    if (shadowCoord.x < 0.0 || 1.0 < shadowCoord.x
        || shadowCoord.y < 0.0 || 1.0 < shadowCoord.y
        || shadowCoord.z < 0.0 || 1.0 < shadowCoord.z)
        return 1.0;
    float shadowFactor = 0.0;
    float bias = _AE_DIRECTIONAL_LIGHT0_SHADOW_BIAS_ * tan(acos(NdotL));
    bias = clamp(bias, 0.0, 1.0);
    if (0.0 < _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFT_) {
        vec2 SAMPLE_OFFSETS[SAMPLE_SIZE];
        SAMPLE_OFFSETS[0] = vec2(-1.0, -1.0); SAMPLE_OFFSETS[1] = vec2(0.0, -1.0); SAMPLE_OFFSETS[2] = vec2(1.0, -1.0);
        SAMPLE_OFFSETS[3] = vec2(-1.0, 0.0);  SAMPLE_OFFSETS[4] = vec2(0.0, 0.0);  SAMPLE_OFFSETS[5] = vec2(1.0, 0.0);
        SAMPLE_OFFSETS[6] = vec2(-1.0, 1.0);  SAMPLE_OFFSETS[7] = vec2(0.0, 1.0);  SAMPLE_OFFSETS[8] = vec2(1.0, 1.0);
        float sum = 0.0;
        vec2 texSizeInv = vec2(1.0) / _AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_SIZE_;
        for (int i = 0; i < SAMPLE_SIZE; ++i) {
            float depth = UnpackFloatFromVec4i(texture2D(_AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_, shadowCoord.xy + SAMPLE_OFFSETS[i] * _AE_DIRECTIONAL_LIGHT0_SHADOW_SOFTNESS_ * texSizeInv));
            sum += float(shadowCoord.z < depth + bias);
        }
        shadowFactor = sum / float(SAMPLE_SIZE);
    }
    else {
        float depth = UnpackFloatFromVec4i(texture2D(_AE_DIRECTIONAL_LIGHT0_SHADOW_TEXTURE_, shadowCoord.xy));
        shadowFactor = float(shadowCoord.z < depth + bias);
    }
    
    if (shadowFactor < 1.0)
        shadowFactor = mix(1.0, shadowFactor, _AE_DIRECTIONAL_LIGHT0_SHADOW_STRENGTH_ * 0.5);
    return shadowFactor;
}
#endif

void main()
{
#ifdef AE_USE_NORMAL_TEXTURE
    vec3 worldTangent  = normalize(v_WorldTangent);
    vec3 worldBinormal = normalize(v_WorldBinormal);
#endif
    vec3 worldNormal   = normalize(v_WorldNormal);
    vec3 worldPosition = v_WorldPosition;
    vec3 worldViewDir  = normalize(u_CameraPosInWorld - worldPosition);
    vec2 texcoord      = v_Texcoord;
    
#ifdef AE_USE_NORMAL_TEXTURE
    mat3 normCoords = mat3(worldTangent, worldBinormal, worldNormal);
    worldNormal = texture2D(u_NormalTex, texcoord).rgb * 2.0 - 1.0;
    worldNormal = normalize(normCoords * worldNormal);
#endif
    
    vec3 albedo;
    float alpha;
#ifdef AE_USE_DIFFUSE_TEXTURE
    vec4 diff = texture2D(u_DiffuseTex, texcoord);
    albedo = diff.rgb * u_Diffuse;
    alpha = u_Alpha * diff.a;
#else
    albedo = u_Diffuse;
    alpha  = u_Alpha;
#endif
    
#ifdef AE_USE_MROO_TEXTURE
    vec4 mroo = texture2D(u_MROOTex, texcoord);
#endif
    
    float metallic;
#ifdef AE_USE_METALLIC_TEXTURE
    metallic = u_Metallic * mroo.r;
#else
    metallic = u_Metallic;
#endif
    
    float roughness;
#ifdef AE_USE_ROUGHNESS_TEXTURE
    roughness = u_Roughness * mroo.g;
#else
    roughness = u_Roughness;
#endif
    
    float occlusion;
#ifdef AE_USE_OCCLUSION_TEXTURE
    occlusion = mroo.b;
#else
    occlusion = 1.0;
#endif
    
#ifdef AE_USE_OPACITY_TEXTURE
    alpha *= mroo.a;
#endif
    
    vec3 finalColor = vec3(0.0);
    
#ifdef AE_USE_LIGHTING
#ifdef _AE_USE_DIRECTIONAL_LIGHTING_
#if 0 < MAX_DIRECTIONAL_LIGHTS_NUM
    vec3 L = normalize(-_AE_DIRECTIONAL_LIGHTS_DIRECTION_[0]);
    float NdotL = max(0.0, dot(worldNormal, L));
    float factor = sign(NdotL);
    float shadow = Shadowing(worldPosition, NdotL);
    gl_FragColor = vec4(0.0, 0.0, 0.0, mix(0.0, (1.0 - shadow), factor));
#endif
#endif
#else
    gl_FragColor = vec4(0.0);
#endif
}
