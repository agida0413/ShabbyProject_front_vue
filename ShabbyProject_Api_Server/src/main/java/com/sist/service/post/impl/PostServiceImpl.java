package com.sist.service.post.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.sist.common.exception.BadRequestException;
import com.sist.common.exception.InternerException;
import com.sist.common.util.PathVariableValidation;
import com.sist.common.util.SimpleCodeGet;
import com.sist.dto.api.ResponseDTO;
import com.sist.dto.post.DoPostLikeDTO;
import com.sist.dto.post.GetPostDetailDTO;
import com.sist.dto.post.PostDelDTO;
import com.sist.dto.post.PostDetailDTO;
import com.sist.dto.post.TagInformDTO;
import com.sist.dto.post.WritePostDTO;
import com.sist.repository.post.PostRepository;
import com.sist.service.image.ImageService;
import com.sist.service.post.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	//게시물 repository
    private final PostRepository postRepository;
    //이미지업로드 서비스
    private final ImageService imageService;


    //게시물 업로드 서비스 
    @Override
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> postInsertTransaction(WritePostDTO dto)  {
    	    	
    	List<MultipartFile> imgList = dto.getImgList(); // 업로드할 이미지 리스트
       
    	List<String> imgUrList = uploadImage(imgList); // 업로드된 이미지의 URL을 저장할 리스트
     
    	 
        // 데이터베이스 저장
        try {
            int idNum = SimpleCodeGet.getIdNum(); // 현재 세션의 ID 가져오기
            dto.setIdNum(idNum); // 게시물 DTO에 ID 설정
            //만약 공백제거 ""면 null
          
            setPostState(dto); // 게시물 상태 설정

            // 게시물 테이블에 데이터 저장
            postRepository.postInsert(dto);
            
            // 관심사 및 인물 태그 인서트 //postnum+hobby  유니크키 
           insertHobbies(dto);
           insertFollowTags(dto);

            // 게시물 이미지 URL 리스트를 DTO에 설정하고 이미지 테이블에 저장
            dto.setImgUrlList(imgUrList);
            postRepository.postImgInsert(dto);

        } catch (Exception e) {
            removeImage(imgUrList); // 데이터베이스 오류 발생 시 이미지 삭제 및 롤백
        }

        // 성공적으로 처리된 경우 HTTP 200 OK 응답 반환
        return new ResponseEntity<>(new ResponseDTO<>(), HttpStatus.OK);
    }

   


    //게시물 상세보기 서비스
	@Override
	public ResponseEntity<ResponseDTO<PostDetailDTO>> postDetail(int postNum) {
		// TODO Auto-generated method stub
		
		//현재 로그인한 회원의 고유번호
		int idNum=SimpleCodeGet.getIdNum();
		
		//데이터베이스 전송객체
		GetPostDetailDTO reqDto= new GetPostDetailDTO();
		reqDto.setPostNum(postNum); //게시물번호
		reqDto.setIdNum(idNum);//회원번호 
		
		//게시물 고유번호 기반 정보 가져오기 
		PostDetailDTO dto = postRepository.postDetail(reqDto);
		//정보가 없을 시
		if(dto==null) {
			throw new BadRequestException("존재하지 않는 게시물 입니다.");
		}
		//현재 로그인한 회원 기반 , 현재 조회하려는 게시물의 좋아요 상태 체크 
		if(dto.getLikeCheck()!=0) {
			//조인한 컬럼(게시물 좋아요테이블) 의 카운트가 0이아니면 like === true
			dto.setLiked(true);
		}
		//만약 내 게시물이라면 
		if(dto.getIdNum()==idNum) {
			dto.setItsMe(true);
		}
		
	
		//데이터베이스에서 , 를 붙혀 스트링 타입으로 받아온 데이터들을 다시 리스트 형태로 변환하고 , 보낼 객체에 담는다 .
		//관심사리스트 생성 
		List<String> hbList = new ArrayList<>();
		//사람태그 리스트 생성
		List<TagInformDTO> tgList=new ArrayList<>();
		//이미지 리스트 생성
		List<String> imgList = new ArrayList<>();
		
		List<String> tagNickList=new ArrayList<>();
		//관심사 리스트 배열로 변환 
		if(dto.getStrHobbyList()!=null) {
			String [] handleHobbyList=dto.getStrHobbyList().split(",");
			//리스트로 변환 
			for (String hobby : handleHobbyList) {
				//하나씩 add
				hbList.add(hobby);
			}
			//객체에 담기 
			dto.setHobbyList(hbList);
		}
		
		if(dto.getStrImgList()!=null) {
			// 이미지 리스트 배열로 변환
			String[] handleImgList = dto.getStrImgList().split(",");
			// 리스트로 변환
			for (String img : handleImgList) {
				// 하나씩 add
				imgList.add(img);
			}
			dto.setImgList(imgList);
		}
		
		if(dto.getStrTagList()!=null) {
			// 이미지 리스트 배열로 변환
			String[] handleTagList = dto.getStrTagList().split(",");
			// 리스트로 변환
			for (String tag : handleTagList) {
				// 하나씩 add
				tagNickList.add(tag);
			}
			dto.setTagNickList(tagNickList);
		}
		
		if(dto.getStrTagList()!=null) {
			// 이미지 리스트 배열로 변환
			System.out.println("테스트"+dto.getStrTagProfiles());
			String[] handleTagList = dto.getStrTagList().split(",");
			String[] handleTagProfileList= dto.getStrTagProfiles().split(",");;
			
			
			
			// 리스트로 변환
			for (int i=0; i<handleTagList.length;i++) {
				// 하나씩 add
				TagInformDTO handleDto = new TagInformDTO();
				handleDto.setNickname(handleTagList[i]);
				if(handleTagProfileList[i].equals("null")) {
					handleDto.setProfile(null);
				}else {
					handleDto.setProfile(handleTagProfileList[i]);
				}
				
				
					
				
				tgList.add(handleDto);
			}
			dto.setTagList(tgList);;
		}
		
		//클라이언트로 보낼때 회원고유번호 감추기 위함 
		dto.setIdNum(0);
		
		// 성공적으로 처리된 경우 HTTP 200 OK 응답 반환
        return new ResponseEntity<ResponseDTO<PostDetailDTO>>
        (new ResponseDTO<PostDetailDTO>(dto), HttpStatus.OK);
	}
	
	//게시물 삭제 
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO<Void>> postDelete(PostDelDTO dto) {
		// TODO Auto-generated method stub
		//현재 로그인 회원 고유번호 
		int idNum=SimpleCodeGet.getIdNum();
		dto.setIdNum(idNum);
		//게시물 번호
		int postNum=dto.getPostNum();
		//해당 게시물에 해당하는 사진 리스트를 가져옴
		List<String> imgUrlList=postRepository.postImgListByPostNum(postNum);
		//게시물 등록시 필히 사진을 1장이상 등록하기 때문에 만약 사진리스트가 비어있으면 예외처리
		if(imgUrlList.size()==0) {
			throw new InternerException("서버내부오류로 게시물삭제에 실패하였습니다.", "게시물 삭제중 이미지 리스트가 비어있음");
		}
		
		//단순히 게시물 번호를 받아 레코드를 삭제할 수 있지만 , 혹시모를 위험에 대비하여 where절에 id넘버를 줘서 조건을 주면
		//본인이 작성한 게시물만 삭제할 수 있게 제한을 둘수 있고 , 이상현상을 막을 수 있다 판단하여 회원고유번호를 준다.
		postRepository.postDelete(dto);
		
		
		//s3 이미지 삭제 == > 삭제 실패시 예외발생으로 어차피 데이터베이스는 롤백된다. 
		removeImage(imgUrlList);
		
		// 성공적으로 처리된 경우 HTTP 200 OK 응답 반환
        return new ResponseEntity<ResponseDTO<Void>>
        (new ResponseDTO<Void>(), HttpStatus.OK);
	}
	
	//게시물 수정 서비스 
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO<Void>> postUpdate(WritePostDTO dto) {
		// TODO Auto-generated method stub
		
		
	//회원 고유번호 	
	int idNum=SimpleCodeGet.getIdNum();
	dto.setIdNum(idNum);
	
	//만약 수정작업시 삭제할 이미지 리스트가 존재하면==>클라이언트에서 기존 이미지를 x버튼을 통해 지운상태로 넘겼다 .
	//removeImgList는 클라이언트에서 삭제한 이미지들의 리스트 
	if(dto.getRemoveImgList().size()!=0) {
		//기존 데이터베이스에서 해당하는 게시물번호와 URL 이름으로 삭제 (URL은 고유하다)
		postRepository.deleteOriginalImg(dto);
		//s3 이미지 삭제 
		removeImage(dto.getRemoveImgList());
	}
	
	//만약 수정시에 새롭게 추가한 이미지가 있다면 
	if(dto.getImgList()!=null&&dto.getImgList().size()!=0) {
		//변경,추가 된사진 s3 업로드 
		List<MultipartFile> imgList = dto.getImgList(); // 업로드할 이미지 리스트
    	List<String> imgUrList = uploadImage(imgList); // 업로드된 이미지의 URL을 저장할 리스트
    	
    	//반환받은 url 리스트 세팅 
    	dto.setImgUrlList(imgUrList);
    	
    	try {
    		//새롭게 추가된 이미지 데이터베이스 저장 
			postRepository.postImgInsert(dto);
		} catch (Exception e) {
			// TODO: handle exception
			//데이터베이스 저장중 예외 발생시 s3 다시 삭제 
			removeImage(imgUrList);
		}
	}
	
	
	//기존 관심사 삭제 
	postRepository.deleteOriginalHobby(dto);
	//기존 회원태그 삭제
	postRepository.deleteOriginalMemTag(dto);
	
	//만약 클라이언트로부터 새롭게 받은 관심사리스트가 비어있지않다면
	//새로운 관심사 인서트 
	if(dto.getHobbyList().size()!=0) {
		postRepository.hobbyInsert(dto);
	}
	//만약 클라이언트로부터 새롭게 받은 맴버태그 리스트가 비어있지않다면
	//새로운 맴버태그 인서트 
	if(dto.getFollowTagList().size()!=0) {
		postRepository.followTagInsert(dto);
	}
	

	//게시물 테이블을 업데이트 할 정보 세팅 
	// 내용 null처리 , boolean값으로 받은(체크박스로 받은) 나만보기 여부 String타입으로 전환 
	setPostState(dto);
	
	//게시물  테이블 업데이트 
	postRepository.postInformUpdate(dto);
	
	
		// 성공적으로 처리된 경우 HTTP 200 OK 응답 반환
	    return new ResponseEntity<ResponseDTO<Void>>
	    (new ResponseDTO<Void>(), HttpStatus.OK);
	}

	
	//게시물 좋아요 작업 
	@Override
	@Transactional
	public ResponseEntity<ResponseDTO<DoPostLikeDTO>> doPostLike(DoPostLikeDTO dto) {
		// TODO Auto-generated method stub
		
		//회원 고유번호를 가져옴 
		int idNum=SimpleCodeGet.getIdNum();
		//dto에 세팅 
		dto.setIdNum(idNum);
		
		//만약 클라이언트로 부터 받은 현재 특정 게시물에 대한 내 좋아요 상태가 
		//true라면 (누른 상태라면)
		if(dto.isLiked()) {
			//좋아요 테이블에서 제거작업 
			postRepository.postLikeDelete(dto);
			//클라이언트에게 보낼 바뀐 상태값
			dto.setLiked(false);
		}//false라면 
		else {
			//좋아요 테이블에 삽입 작업 
			postRepository.postLikeInsert(dto);
			//클라이언트에게 보낼 바뀐 상태값
		    dto.setLiked(true);
		}
		// 회원고유번호 + 게시물 고유번호의 유니크 키 제약조건 == > 이상현상 validation
		
		
		//삽입 후 좋아요 개수를 데이터베이스에서 가져옴 클라이언트에서 단순히 ++, -- 작업을 하면 이상현상이 있을 수 있다 판단 
		int likeCount=postRepository.afterDoPostLike(dto.getPostNum());
		dto.setLikeCount(likeCount);
		
		return new ResponseEntity<ResponseDTO<DoPostLikeDTO>>
		(new ResponseDTO<DoPostLikeDTO>(dto),HttpStatus.OK); //성공 ;
	}
	
	
	
    //게시물의 나만보기, 댓글기능해제 상태를 설정
    private void setPostState(WritePostDTO dto) {
     
        dto.setOnlyMeState(dto.isOnlyMe() ? "ONLYME" : "NOTONLYME"); // 나만 보기 설정
        if (dto.getContent().trim().isEmpty()) {
            dto.setContent(null); // 게시물 내용이 비어있으면 null로 설정
        }
    }

   
    //게시물과 연관된 관심사테이블에 데이터  삽입 
    private void insertHobbies(WritePostDTO dto) {
    	  if (dto.getHobbyList() != null && !dto.getHobbyList().isEmpty()) {
              postRepository.hobbyInsert(dto); // 관심사 테이블에 인서트
          }
    }
    //게시물과 연관된 사람태그 테이블에 데이터  삽입 
    private void insertFollowTags(WritePostDTO dto) {
    	 if (dto.getFollowTagList() != null && !dto.getFollowTagList().isEmpty()) {
             postRepository.followTagInsert(dto); // 인물 태그 테이블에 인서트
         }
  }
     
     
  //s3이미지 삭제
    private void removeImage(List<String> imgUrList) {
    		
    	try {
    		 for (String imgUrl : imgUrList) {
    			
    			 imageService.deleteImage(imgUrl); // S3에서 이미지 삭제
                 
             }
		} catch (InternerException e) {
			// TODO: handle exception
			throw new InternerException(e.getMessage(), e.getServerErrorMsg());
		}
    	 catch (Exception e) {
 			// TODO: handle exception
 			throw new InternerException("서버 내부 오류입니다.", "이미지 업로드 예외발생으로 인한 사진 롤백삭제중 예외발생");
 		}
           
      
    }
    
    private List<String>  uploadImage(List<MultipartFile> imgList) {
    	 List<String> imgUrList = new ArrayList<>(); // 업로드된 이미지의 URL을 저장할 리스트
    	 
    	  // S3 업로드 시작
         try {
         	   for (MultipartFile image : imgList) {
                  
                    String imgUrl = imageService.upload(image); //하나씩 꺼내와서 이미지 업로드==>s3에서 반환받은 url 값 저장
                    imgUrList.add(imgUrl); // 업로드된 이미지를 url리스트에 URL 추가
                }
         
 		} catch (BadRequestException e) {
 			// TODO: handle exception
 			 if (!imgUrList.isEmpty()) {
 			        // 업로드된 이미지가 있을 경우, 필요한 추가 처리 (이미지 삭제)
 			        removeImage(imgUrList);
 			    }
 			throw new BadRequestException(e.getMessage());
 		}
         catch (InternerException e) {
 			// TODO: handle exception
         	 if (!imgUrList.isEmpty()) {
         	        // 업로드된 이미지가 있을 경우, 필요한 추가 처리 ( 이미지 삭제)
         	        removeImage(imgUrList);
         	    }
         	throw new InternerException(e.getMessage(), e.getServerErrorMsg());
 		}
         catch (Exception e) {
 			// TODO: handle exception
         	 if (!imgUrList.isEmpty()) {
         	        // 업로드된 이미지가 있을 경우, 필요한 추가 처리 (예: 이미지 삭제)
         	        removeImage(imgUrList);
         	    }
         	 throw new InternerException("예기치 않은 오류 발생","이미지 삭제중오류 ");
         	
 		}
        
         
         return imgUrList;
    }




	



	
	

}
