package com.serch.server.services.subscription.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.models.subscription.PlanBenefit;
import com.serch.server.models.subscription.PlanChild;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.repositories.subscription.PlanParentRepository;
import com.serch.server.services.subscription.responses.PlanParentResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {PlanImplementation.class})
@ExtendWith(SpringExtension.class)
class PlanImplementationTest {
    @Autowired
    private PlanImplementation planImplementation;

    @MockBean
    private PlanParentRepository planParentRepository;

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans() {
        // Arrange
        when(planParentRepository.findAll()).thenReturn(new ArrayList<>());

        // Act and Assert
        assertThrows(PlanException.class, () -> planImplementation.getPlans());
        verify(planParentRepository).findAll();
    }

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans2() {
        // Arrange
        PlanParent planParent = new PlanParent();
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Plan is empty");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Plan is empty");
        planParent.setId("42");
        planParent.setImage("Plan is empty");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanParent> planParentList = new ArrayList<>();
        planParentList.add(planParent);
        when(planParentRepository.findAll()).thenReturn(planParentList);

        // Act
        ApiResponse<List<PlanParentResponse>> actualPlans = planImplementation.getPlans();

        // Assert
        verify(planParentRepository).findAll();
        assertEquals("Successful", actualPlans.getMessage());
        assertEquals(1, actualPlans.getData().size());
        assertEquals(200, actualPlans.getCode().intValue());
        assertEquals(HttpStatus.OK, actualPlans.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans3() {
        // Arrange
        PlanParent planParent = new PlanParent();
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Plan is empty");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Plan is empty");
        planParent.setId("42");
        planParent.setImage("Plan is empty");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParent planParent2 = new PlanParent();
        planParent2.setAmount("Successful");
        planParent2.setBenefits(new ArrayList<>());
        planParent2.setChildren(new ArrayList<>());
        planParent2.setColor("Color");
        planParent2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent2.setDescription("Successful");
        planParent2.setDuration("Duration");
        planParent2.setId("Successful");
        planParent2.setImage("Image");
        planParent2.setType(PlanType.PAYU);
        planParent2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanParent> planParentList = new ArrayList<>();
        planParentList.add(planParent2);
        planParentList.add(planParent);
        when(planParentRepository.findAll()).thenReturn(planParentList);

        // Act
        ApiResponse<List<PlanParentResponse>> actualPlans = planImplementation.getPlans();

        // Assert
        verify(planParentRepository).findAll();
        assertEquals("Successful", actualPlans.getMessage());
        assertEquals(2, actualPlans.getData().size());
        assertEquals(200, actualPlans.getCode().intValue());
        assertEquals(HttpStatus.OK, actualPlans.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans4() {
        // Arrange
        when(planParentRepository.findAll()).thenThrow(new PlanException("An error occurred"));

        // Act and Assert
        assertThrows(PlanException.class, () -> planImplementation.getPlans());
        verify(planParentRepository).findAll();
    }

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans5() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Successful");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Successful");
        parent.setId("42");
        parent.setImage("Successful");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanBenefit planBenefit = new PlanBenefit();
        planBenefit.setBenefit("Successful");
        planBenefit.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planBenefit.setId(1L);
        planBenefit.setParent(parent);
        planBenefit.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanBenefit> benefits = new ArrayList<>();
        benefits.add(planBenefit);

        PlanParent planParent = new PlanParent();
        planParent.setAmount("10");
        planParent.setBenefits(benefits);
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Plan is empty");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Plan is empty");
        planParent.setId("42");
        planParent.setImage("Plan is empty");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanParent> planParentList = new ArrayList<>();
        planParentList.add(planParent);
        when(planParentRepository.findAll()).thenReturn(planParentList);

        // Act
        ApiResponse<List<PlanParentResponse>> actualPlans = planImplementation.getPlans();

        // Assert
        verify(planParentRepository).findAll();
        assertEquals("Successful", actualPlans.getMessage());
        assertEquals(1, actualPlans.getData().size());
        assertEquals(200, actualPlans.getCode().intValue());
        assertEquals(HttpStatus.OK, actualPlans.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlans()}
     */
    @Test
    void testGetPlans6() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Successful");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Successful");
        parent.setId("42");
        parent.setImage("Successful");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanChild planChild = new PlanChild();
        planChild.setAmount("10");
        planChild.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planChild.setDiscount("3");
        planChild.setId("42");
        planChild.setIsBusiness(true);
        planChild.setName("Successful");
        planChild.setParent(parent);
        planChild.setTag("Successful");
        planChild.setType(SubPlanType.DAILY);
        planChild.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanChild> children = new ArrayList<>();
        children.add(planChild);

        PlanParent planParent = new PlanParent();
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(children);
        planParent.setColor("Plan is empty");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Plan is empty");
        planParent.setId("42");
        planParent.setImage("Plan is empty");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanParent> planParentList = new ArrayList<>();
        planParentList.add(planParent);
        when(planParentRepository.findAll()).thenReturn(planParentList);

        // Act
        ApiResponse<List<PlanParentResponse>> actualPlans = planImplementation.getPlans();

        // Assert
        verify(planParentRepository).findAll();
        assertEquals("Successful", actualPlans.getMessage());
        assertEquals(1, actualPlans.getData().size());
        assertEquals(200, actualPlans.getCode().intValue());
        assertEquals(HttpStatus.OK, actualPlans.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan() {
        // Arrange
        PlanParent planParent = new PlanParent();
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.FREE);

        // Assert
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan2() {
        // Arrange
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(new ArrayList<>());
        when(planParent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.FREE);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan3() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Successful");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Successful");
        parent.setId("42");
        parent.setImage("Successful");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanBenefit planBenefit = new PlanBenefit();
        planBenefit.setBenefit("Successful");
        planBenefit.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planBenefit.setId(1L);
        planBenefit.setParent(parent);
        planBenefit.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanBenefit> planBenefitList = new ArrayList<>();
        planBenefitList.add(planBenefit);
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(planBenefitList);
        when(planParent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.FREE);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(1, data.getBenefits().size());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan4() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Successful");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Successful");
        parent.setId("42");
        parent.setImage("Successful");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanChild planChild = new PlanChild();
        planChild.setAmount("10");
        planChild.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planChild.setDiscount("3");
        planChild.setId("42");
        planChild.setIsBusiness(true);
        planChild.setName("Successful");
        planChild.setParent(parent);
        planChild.setTag("Successful");
        planChild.setType(SubPlanType.DAILY);
        planChild.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanChild> planChildList = new ArrayList<>();
        planChildList.add(planChild);
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(new ArrayList<>());
        when(planParent.getChildren()).thenReturn(planChildList);
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.FREE);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(1, data.getChildren().size());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan5() {
        // Arrange
        Optional<PlanParent> emptyResult = Optional.empty();
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(PlanException.class, () -> planImplementation.getPlan(PlanType.FREE));
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan6() {
        // Arrange
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(new ArrayList<>());
        when(planParent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.PAYU);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan7() {
        // Arrange
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(new ArrayList<>());
        when(planParent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.ALL_DAY);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test: {@link PlanImplementation#getPlan(PlanType)}
     */
    @Test
    void testGetPlan8() {
        // Arrange
        PlanParent planParent = mock(PlanParent.class);
        when(planParent.getType()).thenReturn(PlanType.FREE);
        when(planParent.getAmount()).thenReturn("10");
        when(planParent.getColor()).thenReturn("Color");
        when(planParent.getDescription()).thenReturn("The characteristics of someone or something");
        when(planParent.getDuration()).thenReturn("Duration");
        when(planParent.getId()).thenReturn("42");
        when(planParent.getImage()).thenReturn("Image");
        when(planParent.getBenefits()).thenReturn(new ArrayList<>());
        when(planParent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(planParent).setAmount(Mockito.<String>any());
        doNothing().when(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(planParent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(planParent).setColor(Mockito.<String>any());
        doNothing().when(planParent).setDescription(Mockito.<String>any());
        doNothing().when(planParent).setDuration(Mockito.<String>any());
        doNothing().when(planParent).setId(Mockito.<String>any());
        doNothing().when(planParent).setImage(Mockito.<String>any());
        doNothing().when(planParent).setType(Mockito.<PlanType>any());
        planParent.setAmount("10");
        planParent.setBenefits(new ArrayList<>());
        planParent.setChildren(new ArrayList<>());
        planParent.setColor("Color");
        planParent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planParent.setDescription("The characteristics of someone or something");
        planParent.setDuration("Duration");
        planParent.setId("42");
        planParent.setImage("Image");
        planParent.setType(PlanType.FREE);
        planParent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<PlanParent> ofResult = Optional.of(planParent);
        when(planParentRepository.findByType(Mockito.<PlanType>any())).thenReturn(ofResult);

        // Act
        ApiResponse<PlanParentResponse> actualPlan = planImplementation.getPlan(PlanType.PREMIUM);

        // Assert
        verify(planParent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(planParent).getAmount();
        verify(planParent, atLeast(1)).getBenefits();
        verify(planParent, atLeast(1)).getChildren();
        verify(planParent).getColor();
        verify(planParent).getDescription();
        verify(planParent).getDuration();
        verify(planParent).getId();
        verify(planParent).getImage();
        verify(planParent).getType();
        verify(planParent).setAmount(Mockito.<String>any());
        verify(planParent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(planParent).setChildren(Mockito.<List<PlanChild>>any());
        verify(planParent).setColor(Mockito.<String>any());
        verify(planParent).setDescription(Mockito.<String>any());
        verify(planParent).setDuration(Mockito.<String>any());
        verify(planParent).setId(Mockito.<String>any());
        verify(planParent).setImage(Mockito.<String>any());
        verify(planParent).setType(Mockito.<PlanType>any());
        verify(planParentRepository).findByType(Mockito.<PlanType>any());
        PlanParentResponse data = actualPlan.getData();
        assertEquals("10", data.getAmount());
        assertEquals("42", data.getId());
        assertEquals("Color", data.getColor());
        assertEquals("Duration", data.getDuration());
        assertEquals("Image", data.getImage());
        assertEquals("Successful", actualPlan.getMessage());
        assertEquals("The characteristics of someone or something", data.getDescription());
        assertEquals(200, actualPlan.getCode().intValue());
        assertEquals(PlanType.FREE, data.getType());
        assertEquals(HttpStatus.OK, actualPlan.getStatus());
    }

    /**
     * Method under test:
     * {@link PlanImplementation#updateChildren(PlanParent, PlanParentResponse)}
     */
    @Test
    void testUpdateChildren() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        ArrayList<PlanBenefit> benefits = new ArrayList<>();
        parent.setBenefits(benefits);
        parent.setChildren(new ArrayList<>());
        parent.setColor("Color");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Duration");
        parent.setId("42");
        parent.setImage("Image");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParentResponse response = new PlanParentResponse();
        response.setAmount("10");
        response.setBenefits(new ArrayList<>());
        response.setChildren(new ArrayList<>());
        response.setColor("Color");
        response.setDescription("The characteristics of someone or something");
        response.setDuration("Duration");
        response.setId("42");
        response.setImage("Image");
        response.setType(PlanType.FREE);

        // Act
        planImplementation.updateChildren(parent, response);

        // Assert that nothing has changed
        assertEquals("00:00", parent.getCreatedAt().toLocalTime().toString());
        assertEquals("00:00", parent.getUpdatedAt().toLocalTime().toString());
        assertEquals("10", parent.getAmount());
        assertEquals("10", response.getAmount());
        assertEquals("42", parent.getId());
        assertEquals("42", response.getId());
        assertEquals("Color", parent.getColor());
        assertEquals("Color", response.getColor());
        assertEquals("Duration", parent.getDuration());
        assertEquals("Duration", response.getDuration());
        assertEquals("Image", parent.getImage());
        assertEquals("Image", response.getImage());
        assertEquals("The characteristics of someone or something", parent.getDescription());
        assertEquals("The characteristics of someone or something", response.getDescription());
        assertEquals(PlanType.FREE, parent.getType());
        assertEquals(PlanType.FREE, response.getType());
        List<PlanBenefit> benefits2 = parent.getBenefits();
        assertTrue(benefits2.isEmpty());
        assertEquals(benefits, parent.getChildren());
        assertEquals(benefits2, response.getBenefits());
        assertEquals(benefits2, response.getChildren());
    }

    /**
     * Method under test:
     * {@link PlanImplementation#updateChildren(PlanParent, PlanParentResponse)}
     */
    @Test
    void testUpdateChildren2() {
        // Arrange
        PlanParent parent = mock(PlanParent.class);
        ArrayList<PlanBenefit> planBenefitList = new ArrayList<>();
        when(parent.getBenefits()).thenReturn(planBenefitList);
        when(parent.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(parent).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent).setAmount(Mockito.<String>any());
        doNothing().when(parent).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(parent).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(parent).setColor(Mockito.<String>any());
        doNothing().when(parent).setDescription(Mockito.<String>any());
        doNothing().when(parent).setDuration(Mockito.<String>any());
        doNothing().when(parent).setId(Mockito.<String>any());
        doNothing().when(parent).setImage(Mockito.<String>any());
        doNothing().when(parent).setType(Mockito.<PlanType>any());
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Color");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Duration");
        parent.setId("42");
        parent.setImage("Image");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParentResponse response = new PlanParentResponse();
        response.setAmount("10");
        response.setBenefits(new ArrayList<>());
        response.setChildren(new ArrayList<>());
        response.setColor("Color");
        response.setDescription("The characteristics of someone or something");
        response.setDuration("Duration");
        response.setId("42");
        response.setImage("Image");
        response.setType(PlanType.FREE);

        // Act
        planImplementation.updateChildren(parent, response);

        // Assert that nothing has changed
        verify(parent).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(parent).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(parent, atLeast(1)).getBenefits();
        verify(parent, atLeast(1)).getChildren();
        verify(parent).setAmount(Mockito.<String>any());
        verify(parent).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(parent).setChildren(Mockito.<List<PlanChild>>any());
        verify(parent).setColor(Mockito.<String>any());
        verify(parent).setDescription(Mockito.<String>any());
        verify(parent).setDuration(Mockito.<String>any());
        verify(parent).setId(Mockito.<String>any());
        verify(parent).setImage(Mockito.<String>any());
        verify(parent).setType(Mockito.<PlanType>any());
        assertEquals("10", response.getAmount());
        assertEquals("42", response.getId());
        assertEquals("Color", response.getColor());
        assertEquals("Duration", response.getDuration());
        assertEquals("Image", response.getImage());
        assertEquals("The characteristics of someone or something", response.getDescription());
        assertEquals(PlanType.FREE, response.getType());
        assertEquals(planBenefitList, response.getBenefits());
        assertEquals(planBenefitList, response.getChildren());
    }

    /**
     * Method under test:
     * {@link PlanImplementation#updateChildren(PlanParent, PlanParentResponse)}
     */
    @Test
    void testUpdateChildren3() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Color");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Duration");
        parent.setId("42");
        parent.setImage("Image");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanBenefit planBenefit = new PlanBenefit();
        planBenefit.setBenefit("Benefit");
        planBenefit.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planBenefit.setId(1L);
        planBenefit.setParent(parent);
        planBenefit.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanBenefit> planBenefitList = new ArrayList<>();
        planBenefitList.add(planBenefit);
        PlanParent parent2 = mock(PlanParent.class);
        when(parent2.getBenefits()).thenReturn(planBenefitList);
        when(parent2.getChildren()).thenReturn(new ArrayList<>());
        doNothing().when(parent2).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent2).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent2).setAmount(Mockito.<String>any());
        doNothing().when(parent2).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(parent2).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(parent2).setColor(Mockito.<String>any());
        doNothing().when(parent2).setDescription(Mockito.<String>any());
        doNothing().when(parent2).setDuration(Mockito.<String>any());
        doNothing().when(parent2).setId(Mockito.<String>any());
        doNothing().when(parent2).setImage(Mockito.<String>any());
        doNothing().when(parent2).setType(Mockito.<PlanType>any());
        parent2.setAmount("10");
        parent2.setBenefits(new ArrayList<>());
        parent2.setChildren(new ArrayList<>());
        parent2.setColor("Color");
        parent2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent2.setDescription("The characteristics of someone or something");
        parent2.setDuration("Duration");
        parent2.setId("42");
        parent2.setImage("Image");
        parent2.setType(PlanType.FREE);
        parent2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParentResponse response = new PlanParentResponse();
        response.setAmount("10");
        response.setBenefits(new ArrayList<>());
        response.setChildren(new ArrayList<>());
        response.setColor("Color");
        response.setDescription("The characteristics of someone or something");
        response.setDuration("Duration");
        response.setId("42");
        response.setImage("Image");
        response.setType(PlanType.FREE);

        // Act
        planImplementation.updateChildren(parent2, response);

        // Assert
        verify(parent2).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(parent2).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(parent2, atLeast(1)).getBenefits();
        verify(parent2, atLeast(1)).getChildren();
        verify(parent2).setAmount(Mockito.<String>any());
        verify(parent2).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(parent2).setChildren(Mockito.<List<PlanChild>>any());
        verify(parent2).setColor(Mockito.<String>any());
        verify(parent2).setDescription(Mockito.<String>any());
        verify(parent2).setDuration(Mockito.<String>any());
        verify(parent2).setId(Mockito.<String>any());
        verify(parent2).setImage(Mockito.<String>any());
        verify(parent2).setType(Mockito.<PlanType>any());
        assertEquals(1, response.getBenefits().size());
    }

    /**
     * Method under test:
     * {@link PlanImplementation#updateChildren(PlanParent, PlanParentResponse)}
     */
    @Test
    void testUpdateChildren4() {
        // Arrange
        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Color");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Duration");
        parent.setId("42");
        parent.setImage("Image");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanChild planChild = new PlanChild();
        planChild.setAmount("10");
        planChild.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        planChild.setDiscount("3");
        planChild.setId("42");
        planChild.setIsBusiness(true);
        planChild.setName("Name");
        planChild.setParent(parent);
        planChild.setTag("Tag");
        planChild.setType(SubPlanType.DAILY);
        planChild.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ArrayList<PlanChild> planChildList = new ArrayList<>();
        planChildList.add(planChild);
        PlanParent parent2 = mock(PlanParent.class);
        when(parent2.getBenefits()).thenReturn(new ArrayList<>());
        when(parent2.getChildren()).thenReturn(planChildList);
        doNothing().when(parent2).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent2).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(parent2).setAmount(Mockito.<String>any());
        doNothing().when(parent2).setBenefits(Mockito.<List<PlanBenefit>>any());
        doNothing().when(parent2).setChildren(Mockito.<List<PlanChild>>any());
        doNothing().when(parent2).setColor(Mockito.<String>any());
        doNothing().when(parent2).setDescription(Mockito.<String>any());
        doNothing().when(parent2).setDuration(Mockito.<String>any());
        doNothing().when(parent2).setId(Mockito.<String>any());
        doNothing().when(parent2).setImage(Mockito.<String>any());
        doNothing().when(parent2).setType(Mockito.<PlanType>any());
        parent2.setAmount("10");
        parent2.setBenefits(new ArrayList<>());
        parent2.setChildren(new ArrayList<>());
        parent2.setColor("Color");
        parent2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent2.setDescription("The characteristics of someone or something");
        parent2.setDuration("Duration");
        parent2.setId("42");
        parent2.setImage("Image");
        parent2.setType(PlanType.FREE);
        parent2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParentResponse response = new PlanParentResponse();
        response.setAmount("10");
        response.setBenefits(new ArrayList<>());
        response.setChildren(new ArrayList<>());
        response.setColor("Color");
        response.setDescription("The characteristics of someone or something");
        response.setDuration("Duration");
        response.setId("42");
        response.setImage("Image");
        response.setType(PlanType.FREE);

        // Act
        planImplementation.updateChildren(parent2, response);

        // Assert
        verify(parent2).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(parent2).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(parent2, atLeast(1)).getBenefits();
        verify(parent2, atLeast(1)).getChildren();
        verify(parent2).setAmount(Mockito.<String>any());
        verify(parent2).setBenefits(Mockito.<List<PlanBenefit>>any());
        verify(parent2).setChildren(Mockito.<List<PlanChild>>any());
        verify(parent2).setColor(Mockito.<String>any());
        verify(parent2).setDescription(Mockito.<String>any());
        verify(parent2).setDuration(Mockito.<String>any());
        verify(parent2).setId(Mockito.<String>any());
        verify(parent2).setImage(Mockito.<String>any());
        verify(parent2).setType(Mockito.<PlanType>any());
        assertEquals(1, response.getChildren().size());
    }
}
