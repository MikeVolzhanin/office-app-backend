package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.*;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.repositories.DislikesRepository;
import com.example.officeappbackend.repositories.IdeaPostRepository;
import com.example.officeappbackend.repositories.LikesRepository;
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
        User user = userService.findByEmail(principal.getName()).get();

        ideaPost.setTitle(post.getTitle());
        ideaPost.setUserId(user);
        ideaPost.setContent(post.getContent());
        ideaPost.setOfficeId(officeService.findById(post.getOffice()).get());
        ideaPost.setAttachedImages(convertUrlListToString(post.getAttachedImages()));

        ideaPost.setLikesCount(0);
        ideaPost.setDislikesCount(0);
        ideaPost.setCommentsCount(0);

        ideaPost.setCreatedAt(new Date());

        ideaPostRepository.save(ideaPost);
    }

    @Transactional
    public void updatePost(Long id, EditPostDto post){
        IdeaPost currentPost = ideaPostRepository.findById(id).get();
        currentPost.setTitle(post.getTitle());
        currentPost.setContent(post.getContent());
        currentPost.setAttachedImages(convertUrlListToString(post.getAttachedImages()));

        ideaPostRepository.save(currentPost);
    }

    @Transactional
    public void deletePost(Long id){
        IdeaPost currentPost = ideaPostRepository.findById(id).get();
        likesRepository.deleteByPostId(currentPost);
        dislikesRepository.deleteByPostId(currentPost);
        ideaPostRepository.deleteById(id);
    }

    // Перед тем как ставить лайк, необходимо проверить, может быть он уже стоит и нет необходимости снова его ставить
    @Transactional
    public ResponseEntity<?> likePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();
        User user = userService.findByEmail(principal.getName()).get();

        if(likesRepository.findByUserIdAndPostId(user, post).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

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

    // Проверка на повтор + не увеличивается кол-во в бд поста
    @Transactional
    public ResponseEntity<?> dislikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();
        User user = userService.findByEmail(principal.getName()).get();

        if(dislikesRepository.findByUserIdAndPostId(user, post).isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

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
    public void unlikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();
        User user = userService.findByEmail(principal.getName()).get();
        likesRepository.deleteByUserIdAndPostId(user, post);
        Integer likeCount = post.getLikesCount();
        post.setLikesCount(--likeCount);
        ideaPostRepository.save(post);
    }

    @Transactional
    public void undislikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();
        User user = userService.findByEmail(principal.getName()).get();
        Integer dislikeCount = post.getDislikesCount();
        post.setDislikesCount(--dislikeCount);
        dislikesRepository.deleteByUserIdAndPostId(user, post);
        ideaPostRepository.save(post);
    }

    // Обработать ошибки
    public List<IdeaPostDto> getPosts(Integer page, Integer pageSize, FilterDto filterDto, Principal principal, Long authorId){
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
            List<IdeaPost> list = ideaPostRepository.findByOfficeId(officeService.findById(id).get());
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

        if(filterName.equals("by comments")) {
            Collections.sort(posts, Comparator.comparingInt(IdeaPostDto::getCommentsCount));
        }

        if(filterName.equals("by likes")){
            Collections.sort(posts, Comparator.comparingInt(IdeaPostDto::getLikesCount));
        }

        if(filterName.equals("by dislikes")){
            Collections.sort(posts, Comparator.comparingInt(IdeaPostDto::getDislikesCount));
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

    // дописать проверку like'ов
    public IdeaPostDto convertToIdeaPostDto(IdeaPost ideaPost, Principal principal) {

        IdeaPostDto post = new IdeaPostDto();

        post.setId(ideaPost.getId());
        post.setTitle(ideaPost.getTitle());
        post.setContent(ideaPost.getContent());
        post.setIdeaAuthor(userService.convertToIdeaAuthor(ideaPost.getUserId()));
        post.setOffice(officeService.convertToOfficeDto(ideaPost.getOfficeId()));
        post.setAttachedImages(convertStringToUrlList(ideaPost.getAttachedImages()));

        post.setLikesCount(ideaPost.getLikesCount());
        post.setIsLikePressed(likesRepository.findByUserIdAndPostId(userService.findByEmail(principal.getName()).get(), ideaPost).isPresent());

        post.setDislikesCount(ideaPost.getDislikesCount());
        post.setIsDislikePressed(dislikesRepository.findByUserIdAndPostId(userService.findByEmail(principal.getName()).get(), ideaPost).isPresent());

        post.setCommentsCount(ideaPost.getCommentsCount());
        post.setDate(ideaPost.getCreatedAt());

        return post;
    }

    public String convertUrlListToString(List<String> list){
        return String.join(",", list);
    }
    public List<String> convertStringToUrlList(String links){
        return List.of(links.split(","));
    }
}
