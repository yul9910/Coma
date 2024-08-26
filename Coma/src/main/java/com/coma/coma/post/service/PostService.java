package com.coma.coma.post.service;

import com.coma.coma.post.dto.PostRequestDto;
import com.coma.coma.post.dto.PostResponseDto;
import com.coma.coma.post.entity.Post;
import com.coma.coma.post.mapper.PostMapper;
import com.coma.coma.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    // DI
    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    // create
    public PostResponseDto createPost(Post post) {
        Post saved = postRepository.save(post);
        PostResponseDto postResponseDto = postMapper.toResponseDto(saved);
        return postResponseDto;
    }

    // update
    public PostResponseDto updatePost(Integer postId, PostRequestDto postRequestDto) {
        Post targetPost = postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
        targetPost.update(postRequestDto);
        postRepository.save(targetPost);

        return postMapper.toResponseDto(targetPost);
    }


    // postId로 Post 가져오기
    public PostResponseDto findPost(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException());
        PostResponseDto postResponseDto = postMapper.toResponseDto(post);
        return postResponseDto;
    }

    // 특정 게시판의 모든 포스트 가져오기
    public List<PostResponseDto> findAll(Long boardId) {

        List<Post> posts = postRepository.findAll(boardId);
        List<PostResponseDto> postResponseDtos = posts.stream()
                .map(post -> postMapper.toResponseDto(post))
                .collect(Collectors.toList());
        return postResponseDtos;
    }

    // Id 이용해서 Post 삭제
    public void deletePost(Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException());
        postRepository.delete(post);
    }

    public List<PostResponseDto> findByKeyword(Long boardId, String keyword) {
        List<Post> posts = postRepository.findByKeyword(boardId, keyword);
        List<PostResponseDto> postResponseDtos = posts.stream()
                .map(post -> postMapper.toResponseDto(post))
                .collect(Collectors.toList());
        return postResponseDtos;
    }

    // 페이지네이션 적용
    // 특정 게시판의 포스트 가져오기(pagination 적용)
    public Page<PostResponseDto> getPostsWithPagination(Long boardId, Pageable pageable) {
        Page<Post> postPages = postRepository.getPostsWithPagination(boardId, pageable);
        List<PostResponseDto> responseDtos = postPages.getContent().stream()
                .map(postMapper::toResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(responseDtos, pageable, postPages.getTotalElements());
    }

    // 키워드 이용 검색 - 페이지네이션 적용
    public Page<PostResponseDto> findByKeywordWithPagination(Long boardId, String keyword, Pageable pageable) {
//        List<Post> posts = postRepository.findByKeywordWithPagination(boardId, keyword, page, size );
//        List<PostResponseDto> postResponseDtos = posts.stream()
//                .map(post -> postMapper.toResponseDto(post))
//                .collect(Collectors.toList());
//        int totalPosts = postRepository.countPostsInSearchResult(boardId, keyword);
//        int totalPages = (int) Math.ceil((double) totalPosts / size);
//        return new Page<>(postResponseDtos, page, size, totalPosts, totalPages);
        Page<Post> searchResults = postRepository.findByKeywordWithPagination(boardId, keyword, pageable);
        List<PostResponseDto> searchResultsList = searchResults.getContent().stream()
                .map(postMapper::toResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(searchResultsList, pageable, searchResults.getTotalElements());
    }
}