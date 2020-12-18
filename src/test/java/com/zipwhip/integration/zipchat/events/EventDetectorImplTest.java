package com.zipwhip.integration.zipchat.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.zipwhip.integration.zipchat.domain.Channel;
import com.zipwhip.integration.zipchat.domain.Subscriber;
import com.zipwhip.integration.zipchat.repository.ChannelRepository;
import com.zipwhip.integration.zipchat.repository.SubscriberRepository;
import com.zipwhip.message.domain.InboundMessage;

@RunWith(MockitoJUnitRunner.class)
public class EventDetectorImplTest {

  @Mock
  private SubscriberRepository subscriberRepository;

  @Mock
  private ChannelRepository channelRepository;

  @InjectMocks
  private EventDetectorImpl eventDetector;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private InboundMessage inboundMessage;

  private static final String CHAN_NAME = "zwChat";
  private static final String SENDER_ADDR = "999";

  @Before
  public void setup() {
    when(inboundMessage.getPayload().getSourceAddress()).thenReturn(SENDER_ADDR);
    when(subscriberRepository.findById(anyString())).thenReturn(Optional.of(mock(Subscriber.class)));
    when(channelRepository.findChannelByName(eq(CHAN_NAME))).thenReturn(mock(Channel.class));
  }

  @Test
  public void joinValidChannel() {
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.JOIN.getKeyword() +
        " " + CHAN_NAME);

    Optional<Event> res = eventDetector.detectEvent(inboundMessage);

    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository).findChannelByName(CHAN_NAME);

    assertTrue(res.isPresent());
    assertEquals(EventType.JOIN, res.get().getEventType());
  }

  @Test
  public void joinValidChannelNewUser() {
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.JOIN.getKeyword() +
        " " + CHAN_NAME + " " + "BobTheNewUser");
    reset(subscriberRepository);
    when(subscriberRepository.findById(anyString())).thenReturn(Optional.empty());

    Optional<Event> res = eventDetector.detectEvent(inboundMessage);

    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository).findChannelByName(CHAN_NAME);

    assertTrue(res.isPresent());
    assertEquals(EventType.JOIN, res.get().getEventType());
  }

  @Test
  public void joinInvalidChannel() {
    final String chan = "foo";
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.JOIN.getKeyword() +
        " " + chan);

    assertFalse(eventDetector.detectEvent(inboundMessage).isPresent());

    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository).findChannelByName(chan);
  }

  @Test
  public void leaveValidChannel() {
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.LEAVE.getKeyword() +
        " " + CHAN_NAME);

    Optional<Event> res = eventDetector.detectEvent(inboundMessage);

    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository).findChannelByName(CHAN_NAME);

    assertTrue(res.isPresent());
    assertEquals(EventType.LEAVE, res.get().getEventType());
  }

  @Test
  public void leaveInvalidChannel() {
    final String chan = "foo";
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.LEAVE.getKeyword() +
        " " + chan);

    assertFalse(eventDetector.detectEvent(inboundMessage).isPresent());

    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository).findChannelByName(chan);
  }

  @Test
  public void nonEventMsg() {
    when(inboundMessage.getPayload().getBody()).thenReturn("Type '/join channel_name' to join");
    assertFalse(eventDetector.detectEvent(inboundMessage).isPresent());

    verify(subscriberRepository, never()).findById(anyString());
    verify(channelRepository, never()).findChannelByName(anyString());

  }

  @Test
  public void joinSubscriberNotFound() {
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.JOIN.getKeyword() +
        " " + CHAN_NAME);
    when(subscriberRepository.findById(anyString())).thenReturn(Optional.empty());

    assertFalse(eventDetector.detectEvent(inboundMessage).isPresent());
    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository, never()).findChannelByName(anyString());
  }

  @Test
  public void leaveSubscriberNotFound() {
    when(inboundMessage.getPayload().getBody()).thenReturn("/" + EventType.LEAVE.getKeyword() +
        " " + CHAN_NAME);
    when(subscriberRepository.findById(anyString())).thenReturn(Optional.empty());

    assertFalse(eventDetector.detectEvent(inboundMessage).isPresent());
    verify(subscriberRepository).findById(SENDER_ADDR);
    verify(channelRepository, never()).findChannelByName(anyString());
  }
}