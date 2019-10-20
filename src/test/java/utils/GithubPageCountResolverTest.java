package utils;

import com.platform_lunar.homework.utils.GithubPageCountResolver;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class GithubPageCountResolverTest {

    @Test
    void extractPageCount() {
        var link = "<https://api.github.com/repositories/121395510/contributors?per_page=1&anon=true&page=2>; rel=\"next\", " +
                "<https://api.github.com/repositories/121395510/contributors?per_page=1&anon=true&page=213>; rel=\"last\"";

        Assert.assertEquals(Integer.valueOf("213"), GithubPageCountResolver.extractPageCount(link));
    }
}
