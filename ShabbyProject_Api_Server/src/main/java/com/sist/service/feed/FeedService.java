package com.sist.service.feed;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sist.dto.api.ResponseDTO;
import com.sist.dto.feed.UserFeedInformDTO;
import com.sist.dto.follow.FollowListDTO;
import com.sist.dto.follow.FollowSearchDTO;
import com.sist.dto.feed.UpdateProfileDTO;
import com.sist.dto.member.MemberDTO;
import com.sist.dto.post.PostListDTO;

public interface FeedService {
	//사용자 피드 정보 ==> 게시물 제외
	public ResponseEntity<ResponseDTO<UserFeedInformDTO>> loadUserFeedInfo(String nickname);
	//사용자 피드 정보 == >게시물
	public ResponseEntity<ResponseDTO<List<PostListDTO>>> loadUserFeedPostList(String nickname,int page);
	
	//프로필 이미지를 업데이트 하는 서비스 
	public ResponseEntity<ResponseDTO<Void>> updateProfileImg(MultipartFile file);
	
	//자기소개를 업데이트 하는 서비스 
	public ResponseEntity<ResponseDTO<Void>> updateIntroduce(UpdateProfileDTO dto);
	
	//클라이언트에게 자기소개 변경 컴포넌트에서 현재 자기소개 데이터를 전송
	public ResponseEntity<ResponseDTO<MemberDTO>> getOriginalIntroduce();
	
	
}
