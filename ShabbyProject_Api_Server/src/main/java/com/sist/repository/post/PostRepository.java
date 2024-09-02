package com.sist.repository.post;

import java.util.List;

import com.sist.dto.feed.GetUserFeedInformDTO;
import com.sist.dto.post.PostListDTO;
import com.sist.dto.post.WritePostDTO;

public interface PostRepository {
		//게시물 테이블 삽입
		public void postInsert(WritePostDTO dto);
		//해당 게시물과 연관된 관심사 테이블 삽입
		public void hobbyInsert(WritePostDTO dto);
		//해당 게시물과 연관된 인물태그 삽입
		public void followTagInsert(WritePostDTO dto);
		//해당 게시물과 연관된 사진들 삽입
		public void postImgInsert(WritePostDTO dto);
		//사용자  피드에서 게시물리스트 가져오기
		public List<PostListDTO> postList(GetUserFeedInformDTO dto);
	
}
