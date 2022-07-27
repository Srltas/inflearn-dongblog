package com.dongblog.dongblog.api.repository;

import com.dongblog.dongblog.api.domain.Post;
import com.dongblog.dongblog.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
