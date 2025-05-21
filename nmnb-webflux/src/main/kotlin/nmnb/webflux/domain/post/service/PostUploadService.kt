package nmnb.webflux.domain.post.service

import nmnb.r2dbc.user.R2dbcUser
import nmnb.webflux.domain.post.service.dto.request.PostInfoServiceRequest
import org.springframework.http.codec.multipart.FilePart

interface PostUploadService {
    suspend fun upload(user: R2dbcUser, file: FilePart, request: PostInfoServiceRequest)
}
