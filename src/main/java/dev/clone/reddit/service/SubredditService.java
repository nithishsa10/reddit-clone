package dev.clone.reddit.service;

import dev.clone.reddit.dto.SubredditDto;
import dev.clone.reddit.exception.SpringRedditException;
import dev.clone.reddit.mapper.SubredditMapper;
import dev.clone.reddit.model.Subreddit;
import dev.clone.reddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit save = subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll()
                .stream()
                .map(subredditMapper::mapSubredditToDto)
                .collect(toList());
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit =   subredditRepository.findById(id)
                .orElseThrow( () -> new SpringRedditException("No subreddit found with "));
        return subredditMapper.mapSubredditToDto(subreddit);
    }
}
