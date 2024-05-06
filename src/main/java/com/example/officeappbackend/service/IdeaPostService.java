package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.*;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.repositories.DislikesRepository;
import com.example.officeappbackend.repositories.IdeaPostRepository;
import com.example.officeappbackend.repositories.LikesRepository;
import com.example.officeappbackend.repositories.SuggestedPostRepository;
import com.example.officeappbackend.util.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdeaPostService {
    private final IdeaPostRepository ideaPostRepository;
    private final UserService userService;
    private final OfficeService officeService;
    private final LikesRepository likesRepository;
    private final DislikesRepository dislikesRepository;
    private final Page pageGeneration;
    private final SuggestedPostRepository suggest;
    public Map<Integer, String> getFiltersMap(){
        Map<Integer, String> filters = new HashMap<>();
        filters.put(1, "by comments");
        filters.put(2, "by likes");
        filters.put(3, "by dislikes");
        return filters;
    }

    @Transactional
    public void publishPost(PublishPostDto post, Principal principal){
        IdeaPost ideaPost = new IdeaPost();
        User user = userService.findByEmail(principal.getName()).orElse(null);

        ideaPost.setTitle(post.getTitle());
        ideaPost.setUserId(user);
        ideaPost.setContent(post.getContent());
        ideaPost.setOfficeId(Objects.requireNonNull(userService.findByEmail(principal.getName()).orElse(null)).getOffice());
        ideaPost.setAttachedImages(convertUrlListToString(post.getAttachedImages()));

        ideaPost.setLikesCount(0);
        ideaPost.setDislikesCount(0);
        ideaPost.setCommentsCount(0);

        ideaPost.setCreatedAt(new Date());

        ideaPostRepository.save(ideaPost);
    }

    @Transactional
    public ResponseEntity<?> updatePost(Long id, EditPostDto post){
        IdeaPost currentPost = ideaPostRepository.findById(id).orElse(null);
        if(currentPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        currentPost.setTitle(post.getTitle());
        currentPost.setContent(post.getContent());
        currentPost.setAttachedImages(convertUrlListToString(post.getAttachedImages()));

        ideaPostRepository.save(currentPost);
        return ResponseEntity.ok(new Success(
                "Post was changed successfully",
                new Date())
        );
    }

    @Transactional
    public ResponseEntity<?> deletePost(Long id){
        IdeaPost currentPost = ideaPostRepository.findById(id).orElse(null);
        if(currentPost == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        likesRepository.deleteByPostId(currentPost);
        dislikesRepository.deleteByPostId(currentPost);
        ideaPostRepository.deleteById(id);
        return ResponseEntity.ok(new Success("The post was deleted successfully",
                new Date()
        ));
    }

    @Transactional
    public ResponseEntity<?> likePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).orElse(null);
        User user = userService.findByEmail(principal.getName()).orElse(null);

        if(post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(likesRepository.findByUserIdAndPostId(user, post).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(dislikesRepository.findByUserIdAndPostId(user, post).isPresent())
            undislikePost(id, principal);

        Integer likes = post.getLikesCount();
        post.setLikesCount(++likes);

        Likes likeEntity = new Likes();

        likeEntity.setPostId(post);
        likeEntity.setUserId(user);

        likesRepository.save(likeEntity);
        ideaPostRepository.save(post);
        return ResponseEntity.ok(new Success("Like was added successfully",
                new Date()
        ));
    }

    @Transactional
    public ResponseEntity<?> dislikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).orElse(null);
        User user = userService.findByEmail(principal.getName()).orElse(null);

        if(post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(dislikesRepository.findByUserIdAndPostId(user, post).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(likesRepository.findByUserIdAndPostId(user, post).isPresent())
            unlikePost(id, principal);

        Integer dislike = post.getDislikesCount();

        post.setDislikesCount(++dislike);
        ideaPostRepository.save(post);

        Dislikes dislikeEntity = new Dislikes();

        dislikeEntity.setPostId(post);
        dislikeEntity.setUserId(user);

        dislikesRepository.save(dislikeEntity);
        return ResponseEntity.ok(new Success("Dislike was added successfully",
                new Date()
        ));
    }

    @Transactional
    public ResponseEntity<?> unlikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).orElse(null);
        if(post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        User user = userService.findByEmail(principal.getName()).orElse(null);
        likesRepository.deleteByUserIdAndPostId(user, post);
        Integer likeCount = post.getLikesCount();
        post.setLikesCount(--likeCount);
        ideaPostRepository.save(post);
        return ResponseEntity.ok(new Success("Like was removed successfully",
                new Date()
        ));
    }

    @Transactional
    public ResponseEntity<?> undislikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).orElse(null);
        if(post == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User user = userService.findByEmail(principal.getName()).orElse(null);
        Integer dislikeCount = post.getDislikesCount();
        post.setDislikesCount(--dislikeCount);
        dislikesRepository.deleteByUserIdAndPostId(user, post);
        ideaPostRepository.save(post);

        return ResponseEntity.ok(new Success("Dislike was removed successfully",
                new Date()
        ));
    }

    public List<IdeaPostDto> getPosts(Integer page, Integer pageSize, FilterDto filterDto, Principal principal, Long authorId, Boolean favourite){
        Map<Integer, String> filters = getFiltersMap();
        Integer sortingFilterId = filterDto.getSortingFilterId();
        String filterName;
        if(sortingFilterId == null)
            filterName = "nothing";
        else {
            filterName = filters.get(sortingFilterId);
        }
        List<Long> offices = filterDto.getOfficesId();

        List<IdeaPostDto> posts = new ArrayList<>();

        for(Long id : offices){
            List<IdeaPost> list = ideaPostRepository.findByOfficeId(officeService.findById(id).orElse(null));

            if(!filterDto.getText().isEmpty()){
                for(IdeaPost post : list) {
                    if(post.getTitle().contains(filterDto.getText()))
                        posts.add(convertToIdeaPostDto(post, principal));
                }
            }
            else if(authorId != null){
                for(IdeaPost post : list) {
                    if(Objects.equals(post.getUserId().getId(), authorId))
                        posts.add(convertToIdeaPostDto(post, principal));
                }
            }
            else{
                for(IdeaPost post : list) {
                    posts.add(convertToIdeaPostDto(post, principal));
                }
            }
        }

        return showPostsByContract(posts, filterName, page, pageSize);
    }

    public List<IdeaPostDto> showFavouritePosts(Integer page, Integer pageSize, FilterDto filterDto, Principal principal){
        //заменить на enum
        Map<Integer, String> filters = getFiltersMap();
        Integer sortingFilterId = filterDto.getSortingFilterId();
        String filterName;
        if(sortingFilterId == null)
            filterName = "nothing";
        else {
            filterName = filters.get(sortingFilterId);
        }

        List<Long> offices = filterDto.getOfficesId();
        User user = userService.findByEmail(principal.getName()).orElse(null);
        List<IdeaPostDto> posts = new ArrayList<>();
        List<Likes> likedPosts = user.getLikes();
        List<IdeaPost> ideaPosts = new ArrayList<>();

        for(Likes like : likedPosts)
            ideaPosts.add(like.getPostId());

        for(IdeaPost post : ideaPosts){
            for(Long id : offices){
                if(Objects.equals(post.getOfficeId().getId(), id))
                    posts.add(convertToIdeaPostDto(post, principal));
            }

        }

        return showPostsByContract(posts, filterName, page, pageSize);
    }

    public List<IdeaPostDto> showPostsByContract(List<IdeaPostDto> posts, String filterName, Integer page, Integer pageSize){
        switch (filterName) {
            case "by comments" -> posts.sort(Comparator.comparingInt(IdeaPostDto::getCommentsCount).reversed());
            case "by likes" -> posts.sort(Comparator.comparingInt(IdeaPostDto::getLikesCount).reversed());
            case "by dislikes" -> posts.sort(Comparator.comparingInt(IdeaPostDto::getDislikesCount).reversed());
            default -> posts.sort((post1, post2) -> post2.getDate().compareTo(post1.getDate()));
        }

        int items = posts.size();
        int pages = (int) Math.ceil((double) items / pageSize);

        if(pages < page){
            System.out.println("The number of page is less then required");
            return null;
        }

        int fromInd = pageSize * (page - 1);
        int toInd = fromInd + pageSize;

        if(toInd > items)
            toInd = items;

        if(items == 1)
            return List.of(posts.get(0));

        return posts.subList(fromInd, toInd);
    }

    public ResponseEntity<?> getMyIdeas(Integer page, Integer pageSize, Principal principal){
        User mainUser = userService.findByEmail(principal.getName()).orElse(null);
        List<IdeaPostDto> myIdeas = ideaPostRepository.findByUserId(mainUser).stream().map(ideaPost -> convertToIdeaPostDto(ideaPost, principal)).toList();
        return pageGeneration.generatePages(myIdeas, page, pageSize);
    }

    public ResponseEntity<?> suggestIdeaToMyOffice(Long postId, Principal principal){
        IdeaPost ideaPost = ideaPostRepository.findById(postId).get();
        Office office = userService.findByEmail(principal.getName()).get().getOffice();

        if(suggest.findByPostAndOffice(ideaPost, office).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        SuggestedPosts suggestedPost = new SuggestedPosts();
        suggestedPost.setPost(ideaPost);
        suggestedPost.setOffice(office);
        suggest.save(suggestedPost);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> getSuggested(Integer page, Integer pageSize, Principal principal){
        Office office = userService.findByEmail(principal.getName()).get().getOffice();
        List<SuggestedPosts> suggestedPosts = suggest.findByOffice(office);
        return pageGeneration.generatePages(suggestedPosts, page, pageSize);
    }

    public Filters getFilters(){
        List<OfficeDto> offices = officeService.getAvailableOffices().stream().map(officeService::convertToOfficeDto).collect(Collectors.toList());
        List<SortingFilter> sortingFilters = new ArrayList<>();
        Map<Integer, String> filters = getFiltersMap();
        for(int i = 1; i < 4; i++){
            sortingFilters.add(new SortingFilter(i, filters.get(i)));
        }
        return new Filters(offices, sortingFilters);
    }

    public IdeaPostDto findPostById(Long id, Principal principal){
        Optional<IdeaPost> post = ideaPostRepository.findById(id);
        return post.map(ideaPost -> convertToIdeaPostDto(ideaPost, principal)).orElse(null);
    }

    public IdeaPostDto convertToIdeaPostDto(IdeaPost ideaPost, Principal principal) {

        IdeaPostDto post = new IdeaPostDto();

        post.setId(ideaPost.getId());
        post.setTitle(ideaPost.getTitle());
        post.setContent(ideaPost.getContent());
        post.setIdeaAuthor(userService.convertToIdeaAuthor(ideaPost.getUserId()));
        post.setOffice(officeService.convertToOfficeDto(ideaPost.getOfficeId()));
        post.setAttachedImages(convertStringToUrlList(ideaPost.getAttachedImages()));

        post.setLikesCount(ideaPost.getLikesCount());
        post.setIsLikePressed(likesRepository.findByUserIdAndPostId(userService.findByEmail(principal.getName()).orElse(null), ideaPost).isPresent());

        post.setDislikesCount(ideaPost.getDislikesCount());
        post.setIsDislikePressed(dislikesRepository.findByUserIdAndPostId(userService.findByEmail(principal.getName()).orElse(null), ideaPost).isPresent());

        post.setCommentsCount(ideaPost.getCommentsCount());
        post.setDate(ideaPost.getCreatedAt());

        return post;
    }

    public String convertUrlListToString(List<String> list){
        return String.join(",", list);
    }
    public List<String> convertStringToUrlList(String links){
        if(links.isEmpty())
            return new ArrayList<>();
        return List.of(links.split(","));
    }
}
