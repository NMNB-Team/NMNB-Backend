package nmnb.application.post.service

import nmnb.application.post.service.dto.request.PostPageServiceRequest
import nmnb.application.post.service.dto.response.PostPageResponse

interface PostService {
    fun getPostPage(request: PostPageServiceRequest): PostPageResponse
}
