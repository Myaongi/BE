package Myaong.Gangajikimi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 경로에 대하여 CORS를 적용합니다.
        registry.addMapping("/**")
                // 허용할 Origin 목록입니다. (인증 정보 포함 요청이므로 와일드카드 * 대신 명시된 Origin만 허용)
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "http://localhost:3001",
                        "http://192.168.35.185:3000",
                        "https://fe-admin-myaongi.vercel.app/",
                        "https://fe-admin-8yqpesecv-myaongi.vercel.app" // 요청하신 도메인
                )
                // 허용할 HTTP 메서드 목록입니다.
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                // 모든 헤더를 허용합니다.
                .allowedHeaders("*")
                // 인증 정보 (쿠키, Authorization 헤더 등) 전송을 허용합니다.
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
