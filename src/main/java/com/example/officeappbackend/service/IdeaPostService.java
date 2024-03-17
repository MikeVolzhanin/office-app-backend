package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.*;
import com.example.officeappbackend.dto.*;
import com.example.officeappbackend.repositories.DislikesRepository;
import com.example.officeappbackend.repositories.IdeaPostRepository;
import com.example.officeappbackend.repositories.LikesRepository;
import lombok.RequiredArgsConstructor;
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
        filters.put(4, "nothing");
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
    public void likePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();

        Integer likes = post.getLikesCount();

        User user = userService.findByEmail(principal.getName()).get();

        post.setLikesCount(++likes);

        Likes likeEntity = new Likes();

        likeEntity.setPostId(post);
        likeEntity.setUserId(user);

        likesRepository.save(likeEntity);
        ideaPostRepository.save(post);
    }

    // Проверка на повтор + не увеличивается кол-во в бд поста
    @Transactional
    public void dislikePost(Long id, Principal principal){
        IdeaPost post = ideaPostRepository.findById(id).get();
        User user = userService.findByEmail(principal.getName()).get();

        Integer dislike = post.getDislikesCount();

        post.setDislikesCount(++dislike);
        ideaPostRepository.save(post);

        Dislikes dislikeEntity = new Dislikes();

        dislikeEntity.setPostId(post);
        dislikeEntity.setUserId(user);

        dislikesRepository.save(dislikeEntity);
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
    public List<IdeaPostDto> getPosts(Integer page, Integer pageSize, FilterDto filterDto){
        Map<Integer, String> filters = getFiltersMap();
        Integer sortingFilterId = filterDto.getSortingFilterId();
        List<Long> offices = filterDto.getOfficesId();
        String filterName = filters.get(sortingFilterId);
        List<IdeaPostDto> posts = new ArrayList<>();

        for(Long id : offices){
            posts.addAll(
                    ideaPostRepository.findByOfficeId(officeService.findById(id).get()).stream().map(this::convertToIdeaPostDto).toList()
            );
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
            System.out.println("Кол-во страниц меньше чем запрашивается");
            return null;
        }

        int fromInd = pageSize * (page - 1);
        int toInd = fromInd + pageSize;

        if(toInd > items)
            toInd = items;

        return posts.subList(fromInd, toInd);
    }

    public Filters getFilters(){
        List<OfficeDto> offices = officeService.getAvailableOffices().stream().map(officeService::convertToOfficeDto).collect(Collectors.toList());
        List<SortingFilter> sortingFilters = new ArrayList<>();
        Map<Integer, String> filters = getFiltersMap();
        for(int i = 1; i < 5; i++){
            sortingFilters.add(new SortingFilter(i, filters.get(i)));
        }
        return new Filters(offices, sortingFilters);
    }

    // дописать проверку like'ов
    public IdeaPostDto convertToIdeaPostDto(IdeaPost ideaPost) {
        IdeaPostDto post = new IdeaPostDto();

        post.setId(ideaPost.getId());
        post.setTitle(ideaPost.getTitle());
        post.setContent(ideaPost.getContent());
        post.setUserId(ideaPost.getUserId().getId());
        post.setOfficeId(ideaPost.getOfficeId().getId());
        post.setAttachedImages(convertStringToUrlList(ideaPost.getAttachedImages()));

        post.setLikesCount(ideaPost.getLikesCount());
        post.setIsLikePressed(true);

        post.setDislikesCount(ideaPost.getDislikesCount());
        //dislikesRepository.findByUserIdAndPostId(ideaPost.getUserId(), ideaPost).isPresent()
        post.setIsDislikePressed(true);

        post.setCommentsCount(ideaPost.getCommentsCount());
        post.setCreatedAt(ideaPost.getCreatedAt());

        return post;
    }

    public String convertUrlListToString(List<String> list){
        return String.join(",", list);
    }
    public List<String> convertStringToUrlList(String links){
        return List.of(links.split(","));
    }
}
