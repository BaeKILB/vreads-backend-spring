package com.vreads.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vreads.backend.handler.MemberHandler;
import com.vreads.backend.service.MemberService;
import com.vreads.backend.service.VreadsService;
import com.vreads.backend.vo.MemberVO;
import com.vreads.backend.vo.ResEntityDto;
import com.vreads.backend.vo.VreadsVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173, https://vreads-app.web.app/") // CORS 허용을 위한 url 추가
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=UTF-8")
public class VreadsApiController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MemberHandler memHandler;
	
	@Autowired
	private MemberService memService;
	
	@Autowired
	private VreadsService vdService;
	
	@Value("${backUrl}")
	String backUrl;
	@Value("${uploadDir}")
	String uploadDir;
	@Value("${backUrlFolder}")
	String backUrlFolder;
	
	@PostMapping("/api/vread/")
	public ResponseEntity<ResEntityDto> temp(Principal principal, @RequestParam Map<String,String> map) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		resDto.state = "true";
		resDto.data = "";
		resDto.msg = "테스트";
		resDto.error = "";
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	
	@PostMapping("/api/vread/all")
	public ResponseEntity<ResEntityDto> vreadAllLoad(Principal principal) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// vread 가져오기
		vreads = vdService.getAllVreads();
		

		resDto.state = "true";
		resDto.data = vreads;
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	
	// userSearchType 
	// = 0 : uid 검색
	// = 1 : id 로 검색
	@PostMapping("/api/vread/user")
	public ResponseEntity<ResEntityDto> userVreadLoad(Principal principal, 
			@RequestBody Map<String, String> map
			) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String authUserId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// db 검색시 이용할 값 셋팅
		String userId = map.get("userId");
		String userSearchType = map.get("userSearchType");
		String keyword = map.get("keyword");
		int searchType  = 0 ;
		int startCount = vdService.START_COUNT_INIT;
		int setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		try {
			 searchType  = Integer.parseInt(map.get("searchType")) ;
			 startCount = Integer.parseInt(map.get("startCount"));
			 setPageListLimit = Integer.parseInt(map.get("setPageListLimit"));
			
		}catch(Exception e) {
			logger.error(e.getMessage());
			 searchType  = 0 ;
			 startCount = vdService.START_COUNT_INIT;
			 setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		}
		
		// vread 가져오기
		MemberVO member = null;
		int uid = -1;
		if(
				userSearchType == null 
			|| 
			(!userSearchType.equals("0") && !userSearchType.equals("1"))
		) {
			logger.error("검색 조건 정보가 없습니다");
			resDto.state = "false";
			resDto.error = "검색 조건 정보가 없습니다";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
			
		try {			
			if(userSearchType.equals("0")) {
				uid = Integer.parseInt(userId);
			}
			else if(userSearchType.equals("1")) {
				member = memService.findMember(userId);
				uid = member.getMem_idx();
			}
		}
		catch(Exception e) {
			logger.error("유저 값에 이상이 있습니다");
			resDto.state = "false";
			resDto.error = "유저 값에 이상이 있습니다";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		vreads = vdService.getUserVreads(uid,keyword,searchType,startCount,setPageListLimit);
		
		resDto.state = "true";
		resDto.data = vreads;
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	

	/*
	 * <!-- serchType -->
	<!-- 1 = 제목검색 -->
	<!-- 2 = 제목과 타이틀 검색 -->
	<!-- 3 = 서브태그 검색 -->
	 * */
	@PostMapping("/api/vread/search")
	public ResponseEntity<ResEntityDto> vreadSearchKeyword(Principal principal, 
			@RequestBody Map<String, String> map
			) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String authUserId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// db 검색시 이용할 값 셋팅
		String keyword = map.get("keyword");
		int searchType  = 0;
		int startCount = vdService.START_COUNT_INIT;
		int setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		
		try {
			 searchType  = Integer.parseInt(map.get("searchType")) ;
			 startCount = Integer.parseInt(map.get("startCount"));
			 setPageListLimit = Integer.parseInt(map.get("setPageListLimit"));
			
		}catch(Exception e) {
			logger.error(e.getMessage());
			 searchType  = 0 ;
			 startCount = vdService.START_COUNT_INIT;
			 setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		}
		// vread 가져오기

		// 해당 검색어에 해당하는 vreads 가져오기
		vreads = vdService.getVreadsSearch(keyword, searchType, startCount, setPageListLimit);
		
		resDto.state = "true";
		resDto.data = vreads;
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	
	// vread idx 를 이용하여 가져오기
	@PostMapping("/api/vread/detail")
	public ResponseEntity<ResEntityDto> vreadDetail(Principal principal, 
			@RequestBody Map<String, String> map
			) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		VreadsVO vread = null;		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String authUserId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// db 검색시 이용할 값 셋팅
		String vread_idxStr = map.get("vread_idx");
		Long vread_idx = 0L;
		try {
			vread_idx = Long.parseLong(vread_idxStr);
		}catch(Exception e) {
			logger.error(e.getMessage());
			resDto.state = "false";
			resDto.error = "검색 값에 이상이 있습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);
		}
		// vread 가져오기

		// 해당 검색어에 해당하는 vreads 가져오기
		vread = vdService.getVreadIdx(vread_idx);
		
		resDto.state = "true";
		resDto.data = vread;
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	
	
	// ================ 등록 수정 ==================
	
	// 이미지 업로드를 같이하기 위해 form-data 방식으로 데이터 받아옴
	@PostMapping("/api/vread/addVread")
	public ResponseEntity<ResEntityDto> addVread(Principal principal
			, @RequestParam String vd_vtTitle
			, @RequestParam String vd_vtDetail
			, @RequestParam String vd_subtag
			, @RequestParam(value = "file1" , required = false) MultipartFile file1
//			, @RequestBody  Map<String,String> map
			) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();

		if(principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// 데이터 담을 VreadsVO
		VreadsVO vread = new VreadsVO();
		
		// 위에서 가져온 아이디로 idx 찾기
		MemberVO member = memService.findMember(userId);
		
		// 아이디가 없으면 ...
		if(member == null) {
			logger.error("vread 등록에 실패했습니다! 등록된 아이디가 아닙니다! ");
			resDto.state = "false";
			resDto.error = "vread 등록에 실패했습니다! 등록된 아이디가 아닙니다! ";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		int idx = member.getMem_idx();
		
		
		// 이미지 업로드
		// 폴더 없으면 우선 폴더부터 만들기

		// 폴더 만들면서 경로 셋팅
		
		//저장 경로
		String saveDir = "/" + backUrlFolder + uploadDir;
		
		// 저장경로에 날짜 붙여줄때 쓰는 문자열
		String subDir = "";
		try {
	           Date date = new Date();
	           SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	           subDir = sdf.format(date);
	           saveDir +=  subDir;
	           Path path = Paths.get(saveDir);
	           Files.createDirectories(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		// VreadsVO 에 만들어둔 이미지 객체 보내기
		MultipartFile mFile1 = null;
		String imageName1 = "";
		
		if(file1 != null) {
			mFile1 = file1;
			
	        // 파일명 중복방지 UUID
	        String uuid = UUID.randomUUID().toString();
	        
	        // 이미지 명 초기화
	        vread.setVd_media_1("");
	        
	        // 위의 UUID 를 붙여 중복 방지
	        imageName1 = uuid.substring(0, 8) + "_" + mFile1.getOriginalFilename();
		      
	        // VreadsVO 에 들어갈 경로 셋팅
			if(!mFile1.getOriginalFilename().equals("")) vread.setVd_media_1(backUrl + saveDir + "/" + imageName1);        
		      
		}
		

		vread.setMem_idx(idx);
//		vread.setVd_vtTitle(map.get("vd_vtTitle"));
//		vread.setVd_vtDetail(map.get("vd_vtDetail"));
//		vread.setVd_subtag(map.get("vd_subtag"));
		vread.setVd_vtTitle(vd_vtTitle);
		vread.setVd_vtDetail(vd_vtDetail);
		vread.setVd_subtag(vd_subtag);
		try {
			
			if(!vdService.addVread(vread)) {
				logger.error("vread 등록에 실패했습니다!");
				resDto.state = "false";
				resDto.error = "vread 등록에 실패했습니다!";
				return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
			}
			else {
				//이미지 업로드
				if(file1 != null) {
				    try {
						if (!mFile1.getOriginalFilename().equals("")) mFile1.transferTo(new File(saveDir + "/", imageName1));
				    } catch (IllegalStateException e) {
						e.printStackTrace();
				    } catch (IOException e) {
						e.printStackTrace();
				    }
				}
			}
		}
		catch(Exception e) {			
			logger.error(e.getMessage());
			logger.error("vread 등록에 실패했습니다!");
			resDto.state = "false";
			resDto.error = "vread 등록에 실패했습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}

		resDto.state = "true";
		resDto.data = vread.getVreads_idx();
		resDto.msg = "vread 등록 성공";
		resDto.error = "";
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
	

}



