package elastic.search.demo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import elastic.search.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ElasticsearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void indexUser(User user) {
        try {
            elasticsearchClient.index(i -> i
                    .index("users")
                    .id(user.getId())
                    .document(user)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();

        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("name", "email")
                                            .query(keyword).fuzziness("auto")
                                    )
                            ),
                    User.class
            );

            for (Hit<User> hit : response.hits().hits()) {
                users.add(hit.source());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return users;
    }
}
