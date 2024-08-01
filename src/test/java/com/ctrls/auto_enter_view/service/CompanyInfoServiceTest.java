package com.ctrls.auto_enter_view.service;

import static com.ctrls.auto_enter_view.enums.UserRole.ROLE_COMPANY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrls.auto_enter_view.component.KeyGenerator;
import com.ctrls.auto_enter_view.dto.company.CreateCompanyInfoDto.Request;
import com.ctrls.auto_enter_view.dto.company.ReadCompanyInfoDto.Response;
import com.ctrls.auto_enter_view.entity.CompanyEntity;
import com.ctrls.auto_enter_view.entity.CompanyInfoEntity;
import com.ctrls.auto_enter_view.enums.ErrorCode;
import com.ctrls.auto_enter_view.exception.CustomException;
import com.ctrls.auto_enter_view.repository.CompanyInfoRepository;
import com.ctrls.auto_enter_view.repository.CompanyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class CompanyInfoServiceTest {

  @Mock
  private CompanyInfoRepository companyInfoRepository;

  @Mock
  private CompanyRepository companyRepository;

  @Mock
  private KeyGenerator keyGenerator;

  @InjectMocks
  private CompanyInfoService companyInfoService;

  @Captor
  ArgumentCaptor<CompanyInfoEntity> companyInfoCaptor;

  // setup
  private final UserDetails companyDetails = new User("company@naver.com", "testPassword",
      List.of(new SimpleGrantedAuthority(ROLE_COMPANY.name())));

  private final String testKey = "testKey";
  private final String companyKey = "companyKey";
  private final String wrongCompanyKey = "wrongCompanyKey";

  private final CompanyEntity companyEntity = CompanyEntity.builder()
      .companyKey(companyKey)
      .build();

  @Test
  @DisplayName("회사정보 생성_성공")
  void createInfo_Success() {
    // given
    Request request = Request.builder().build();

    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(
        Optional.of(companyEntity));
    when(companyInfoRepository.existsByCompanyKey(companyKey)).thenReturn(false);
    when(keyGenerator.generateKey()).thenReturn(testKey);

    // when
    companyInfoService.createInfo(companyDetails, companyKey, request);

    // then
    verify(companyInfoRepository, times(1)).save(companyInfoCaptor.capture());

    assertEquals(testKey, companyInfoCaptor.getValue().getCompanyInfoKey());
  }

  @Test
  @DisplayName("회사정보 생성_실패_NoAuthority")
  void createInfo_Fail_NoAuthority() {
    // given
    Request request = Request.builder().build();

    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(
        Optional.of(companyEntity));

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> companyInfoService.createInfo(companyDetails, wrongCompanyKey, request));

    // then
    verify(companyInfoRepository, times(0)).save(any());

    assertEquals(ErrorCode.NO_AUTHORITY, exception.getErrorCode());
  }

  @Test
  @DisplayName("회사정보 조회_성공")
  void readInfo_Success() {
    // given
    String companyName = "companyName";

    CompanyInfoEntity companyInfoEntity = CompanyInfoEntity.builder()
        .companyName(companyName)
        .build();

    when(companyInfoRepository.findByCompanyKey(companyKey)).thenReturn(
        Optional.of(companyInfoEntity));

    // when
    Response response = companyInfoService.readInfo(companyKey);

    // then
    assertEquals(companyName, response.getCompanyName());
  }

  @Test
  @DisplayName("회사정보 수정_성공")
  void updateInfo_Success() {
    // given
    String url = "url";
    String newUrl = "newUrl";

    Request request = Request.builder()
        .companyUrl(newUrl)
        .build();

    CompanyInfoEntity companyInfoEntity = CompanyInfoEntity.builder()
        .companyUrl(url)
        .build();

    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(
        Optional.of(companyEntity));
    when(companyInfoRepository.findByCompanyKey(companyKey)).thenReturn(
        Optional.of(companyInfoEntity));

    // when
    companyInfoService.updateInfo(companyDetails, companyKey, request);

    // then
    verify(companyInfoRepository, times(1)).save(companyInfoEntity);

    assertEquals(newUrl, companyInfoEntity.getCompanyUrl());
  }

  @Test
  @DisplayName("회사정보 수정_실패_NotFound")
  void updateInfo_Fail_NotFound() {
    // given
    Request request = Request.builder().build();

    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(
        Optional.of(companyEntity));
    when(companyInfoRepository.findByCompanyKey(companyKey)).thenReturn(Optional.empty());

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> companyInfoService.updateInfo(companyDetails, companyKey, request));

    // then
    assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("회사정보 삭제_성공")
  void deleteInfo_Success() {
    // given
    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(
        Optional.of(companyEntity));

    // when
    companyInfoService.deleteInfo(companyDetails, companyKey);

    // then
    verify(companyInfoRepository, times(1)).deleteByCompanyKey(companyKey);
  }

  @Test
  @DisplayName("회사정보 삭제_실패_EmailNotFound")
  void deleteInfo_Fail_EmailNotFound() {
    // given

    when(companyRepository.findByEmail(companyDetails.getUsername())).thenReturn(Optional.empty());

    // when
    CustomException exception = assertThrows(CustomException.class,
        () -> companyInfoService.deleteInfo(companyDetails, companyKey));

    // then
    assertEquals(ErrorCode.EMAIL_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("회사정보 조회_성공_Empty")
  void readInfo_Success_Empty() {
    // given
    String companyKey = "companyKey";

    // when
    when(companyInfoRepository.findByCompanyKey(companyKey)).thenReturn(Optional.empty());

    // execute
    Response response = companyInfoService.readInfo(companyKey);

    // then
    assertEquals(0, response.getEmployees());
  }

  @Test
  @DisplayName("회사 정보 삭제 : 실패 - 회사 계정을 찾을 수 없음")
  void testDeleteInfo_CompanyNotFoundFailure() {

    String companyKey = "companyKey";
    String email = "test@example.com";

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn(email);
    when(companyRepository.findByEmail(email)).thenReturn(Optional.empty());

    CustomException exception = assertThrows(CustomException.class, () ->
        companyInfoService.deleteInfo(userDetails, companyKey)
    );

    assertEquals("가입된 사용자 이메일이 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("회사 정보 삭제 : 실패 - 권한 없음")
  void testDeleteInfo_NoAuthorityFailure() {

    String companyKey1 = "companyKey1";
    String email = "test@example.com";
    String companyKey2 = "companyKey2";

    CompanyEntity companyEntity = CompanyEntity.builder()
        .email(email)
        .companyKey(companyKey2)
        .companyName("TestCompany")
        .role(ROLE_COMPANY)
        .companyNumber("02-0000-0000")
        .password("Password123!")
        .build();

    UserDetails userDetails = mock(UserDetails.class);
    when(userDetails.getUsername()).thenReturn(email);
    when(companyRepository.findByEmail(email)).thenReturn(Optional.of(companyEntity));

    CustomException exception = assertThrows(CustomException.class, () ->
        companyInfoService.deleteInfo(userDetails, companyKey1)
    );

    assertEquals("권한이 없습니다.", exception.getMessage());
  }
}