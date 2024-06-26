package toyproject.studyscheduler.token.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@NoArgsConstructor
@Getter
@RedisHash(value = "blackToken")
public class BlackToken {

    @Id
    private String token;

    @TimeToLive
    private Long expirationSec;

    public BlackToken(String token, Long expirationSec) {
        this.token = token;
        this.expirationSec = expirationSec;
    }
}
